package edu.wpi.always.calendar;

import com.google.gson.*;
import edu.wpi.always.Always;
import edu.wpi.always.calendar.schema.CalendarSchema;
import edu.wpi.always.client.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.disco.rt.behavior.BehaviorBuilder;
import org.joda.time.*;
import org.joda.time.format.*;
import java.util.*;

/*
 Note this class does not implement {@link ClientPlugin} because
 all of the client-specific actions happen in the enter() and nextState() methods
 of adjacency pairs and are therefore called during the {@link CalendarSchema}'s run() method (via
 MenuTurnStateMachine).  This also means that these actions are not "interruptible"
 by the arbitrator's resource reassignment.
 */
public class CalendarClient implements CalendarUI {

   private static final String PLUGIN_NAME = "calendar";
   private static final String MSG_CALENDAR_DISPLAY = "calendar.display";
   private static final String MSG_CALENDAR_ENTRY_SELECTED = "calendar.entrySelected";
   private static final String MSG_CALENDAR_DAY_SELECTED = "calendar.daySelected";
   private static final DateTimeFormatter WEEK_DATE_FORMAT = DateTimeFormat
         .forPattern("MMMM d, yyyy");
   private static final DateTimeFormatter DAY_DATE_FORMAT = DateTimeFormat
         .forPattern("EEEE, MMMM d");
   private static final DateTimeFormatter EVENT_TIME_FORMAT = DateTimeFormat
         .forPattern("h:mm");
   private static final DateTimeFormatter EVENT_DAY_FORMAT = DateTimeFormat
         .forPattern("EEE");
   private static final DateTimeFormatter EVENT_MONTH_FORMAT = DateTimeFormat
         .forPattern("MMMM yyyy");
   private static final DateTimeFormatter MONTH_DAY_FORMAT = DateTimeFormat
         .forPattern("EEEE");
   private final Calendar calendar;
   private final UIMessageDispatcher dispatcher;
   private CalendarUIListener listener;

   private static CalendarClient THIS;
   
   public CalendarClient (final Calendar calendar,
         UIMessageDispatcher dispatcher) {
      this.calendar = calendar;
      this.dispatcher = dispatcher;
      THIS = this;
      dispatcher.registerReceiveHandler(MSG_CALENDAR_ENTRY_SELECTED,
            new MessageHandler() {

         @Override
         public void handleMessage (JsonObject body) {
            if ( listener != null ) {
               String id = body.get("id").getAsString();
               CalendarEntry entry = calendar.retrieveById(UUID
                     .fromString(id));
               listener.entrySelected(entry);
            }
         }
      });
      dispatcher.registerReceiveHandler(MSG_CALENDAR_DAY_SELECTED,
            new MessageHandler() {

         @Override
         public void handleMessage (JsonObject body) {
            if ( listener != null ) {
               long id = body.get("id").getAsLong();
               listener.daySelected(new LocalDate(id));
            }
         }
      });
   }

   // static methods for use in _CalendarInterruption
   
   public static void showToday () {
      Always.THIS.getCM().getContainer().getComponent(CalendarClient.class); // force THIS 
      Message save = THIS.lastShowMessage; // remember calendar plugin display state
      try { THIS.showDay(new LocalDate(), null, false); } 
      finally { THIS.lastShowMessage = save; }
   }
   
   public static void hideToday () {
      ClientPluginUtils.hidePlugin(getDispatcher());
      ActivitySchema interrupted = SessionSchema.getInterruptedSchema();
      if ( CalendarSchema.class.isInstance(interrupted) ) 
         // interrupted calendar, so put display back where it was
         getDispatcher().send(THIS.lastShowMessage);   
      else if ( SessionSchema.getInterruptedPluginName() != null )
         ClientPluginUtils.startPlugin(getDispatcher(), SessionSchema.getInterruptedPluginName(),
                                         InstanceReuseMode.Reuse, null);
   }
   
   private static UIMessageDispatcher getDispatcher () {
      return Always.THIS.getCM().getContainer().getComponent(UIMessageDispatcher.class);
   }

   private boolean firstTime = true;
   
   public void show () {
      ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
            firstTime ? InstanceReuseMode.Remove : InstanceReuseMode.Reuse, null);
      firstTime = false;
   }
   
   private Message lastShowMessage;
   
   @Override
   public void showDay (LocalDate day, CalendarUIListener listener,
         boolean touchable) {
      this.listener = listener;
      show();
      String label = WEEK_DATE_FORMAT.print(day);
      DateMidnight dayInstant = CalendarUtils.toMidnight(day);
      Message m = Message.builder(MSG_CALENDAR_DISPLAY).add("type", "day")
            .add("label", label)
            .add("dayData", getDayData(dayInstant, touchable)).build();
      lastShowMessage = m;
      dispatcher.send(m);
   }

   @Override
   public void showWeek (LocalDate startDay, CalendarUIListener listener,
         boolean touchable) {
      this.listener = listener;
      show();
      startDay = CalendarUtils.getFirstDayOfWeek(startDay);
      String weekLabel = WEEK_DATE_FORMAT.print(startDay) + " - "
         + WEEK_DATE_FORMAT.print(startDay.plusDays(6));
      DateMidnight dayInstant = CalendarUtils.toMidnight(startDay);
      Message m = Message.builder(MSG_CALENDAR_DISPLAY).add("type", "week")
            .add("weekLabel", weekLabel)
            .add("dayData", getWeekDayData(dayInstant, touchable)).build();
      lastShowMessage = m;
      dispatcher.send(m);
   }

   public JsonElement getWeekDayData (DateMidnight startDay, boolean touchable) {
      JsonArray weekDayData = new JsonArray();
      DateMidnight day = startDay;
      for (int i = 0; i < 7; ++i) {
         weekDayData.add(getDayData(day, touchable));
         day = day.plusDays(1);
      }
      weekDayData.add(getNextWeekDayData(startDay, touchable));
      return weekDayData;
   }

   public int getWeeksInMonth (LocalDate startDay) {
      int numWeeks = 0;
      for (LocalDate day = CalendarUtils.getFirstDayOfWeek(startDay
            .withDayOfMonth(1)); day.getMonthOfYear() == startDay
            .getMonthOfYear() || day.isBefore(startDay); day = day.plusWeeks(1))
         ++numWeeks;
      return numWeeks;
   }

   @Override
   public void showMonth (LocalDate startDay, CalendarUIListener listener) {
      this.listener = listener;
      show();
      startDay = startDay.withDayOfMonth(1);
      int numWeeks = getWeeksInMonth(startDay);
      Message m = Message.builder(MSG_CALENDAR_DISPLAY).add("type", "month")
            .add("monthLabel", EVENT_MONTH_FORMAT.print(startDay))
            .add("dayLabels", getMonthDaysOfWeek(startDay))
            .add("numRows", numWeeks)
            .add("dayData", getMonthDayData(startDay, numWeeks)).build();
      lastShowMessage = m;
      dispatcher.send(m);
   }

   public JsonElement getMonthDaysOfWeek (LocalDate day) {
      day = CalendarUtils.withDayOfWeek(day, 1);
      JsonArray monthDays = new JsonArray();
      for (int c = 0; c < 7; ++c) {
         monthDays.add(new JsonPrimitive(MONTH_DAY_FORMAT.print(day)));
         day = day.plusDays(1);
      }
      return monthDays;
   }

   public JsonElement getMonthDayData (LocalDate startDay, int numWeeks) {
      LocalDate day = CalendarUtils.getFirstDayOfWeek(startDay);
      JsonArray monthDayData = new JsonArray();
      for (int r = 0; r < numWeeks; ++r) {
         for (int c = 0; c < 7; ++c) {
            monthDayData.add(asMonthJson(r, c,
                  CalendarUtils.toMidnight(startDay),
                  CalendarUtils.toMidnight(day)));
            day = day.plusDays(1);
         }
      }
      return monthDayData;
   }

   private JsonElement getDayData (DateMidnight day, boolean touchable) {
      JsonArray dayEvents = new JsonArray();
      List<CalendarEntry> entries = calendar.retrieve(new Interval(day, day
            .plusDays(1)));
      for (CalendarEntry entry : entries)
         dayEvents.add(asJson(entry));
      JsonObject dayData = new JsonObject();
      dayData.addProperty("date", DAY_DATE_FORMAT.print(day));
      dayData.addProperty("isToday", new DateMidnight().equals(day));
      dayData.addProperty("isTouchable", touchable);
      dayData.add("entries", dayEvents);
      return dayData;
   }

   private JsonElement getNextWeekDayData (DateMidnight day, boolean touchable) {
      JsonArray dayEvents = new JsonArray();
      List<CalendarEntry> entries = calendar.retrieve(new Interval(day
            .plusWeeks(1), day.plusWeeks(2)));
      for (CalendarEntry entry : entries)
         dayEvents.add(asWeekJson(entry));
      JsonObject dayData = new JsonObject();
      dayData.addProperty("date", "Next Week");
      dayData.addProperty("isToday", false);
      dayData.addProperty("isTouchable", touchable);
      dayData.add("entries", dayEvents);
      return dayData;
   }

   private JsonElement asJson (CalendarEntry src) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", src.getId().toString());
      jsonObject.addProperty("title", src.getDisplayTitle());
      jsonObject.addProperty("when", EVENT_TIME_FORMAT.print(src.getStart()));
      DateTime start = src.getStart();
      double startTime = start.getHourOfDay() + start.getMinuteOfHour() / 60d;
      jsonObject.addProperty("start", startTime);
      return jsonObject;
   }

   private JsonElement asWeekJson (CalendarEntry src) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", src.getId().toString());
      jsonObject.addProperty("title", src.getDisplayTitle());
      jsonObject.addProperty("when", EVENT_DAY_FORMAT.print(src.getStart()));
      DateTime start = src.getStart();
      jsonObject.addProperty("start", CalendarUtils.getDayOfWeek(start) - 1);
      return jsonObject;
   }

   private JsonElement asMonthJson (int row, int col, DateMidnight monthStart,
         DateMidnight day) {
      List<CalendarEntry> entries = calendar.retrieve(day, day.plusDays(1));
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", day.getMillis());
      jsonObject.addProperty("row", row);
      jsonObject.addProperty("col", col);
      jsonObject.addProperty("hasEvents", entries.size() > 0);
      jsonObject.addProperty("isThisMonth",
            day.getMonthOfYear() == monthStart.getMonthOfYear());
      jsonObject.addProperty("isToday", new DateMidnight().equals(day));
      jsonObject.addProperty("label", day.getDayOfMonth());
      return jsonObject;
   }

}
