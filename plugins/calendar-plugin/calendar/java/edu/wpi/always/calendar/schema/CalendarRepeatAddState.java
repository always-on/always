package edu.wpi.always.calendar.schema;

import edu.wpi.always.Always;
import edu.wpi.always.calendar.CalendarPlugin;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.Cancel;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventDayAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventPersonAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventTypeAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.HowLongAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.TimeAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.WhereAdjacencyPair;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.calendar.CalendarEntryTypeManager.Types;
import edu.wpi.always.user.people.Person;
import edu.wpi.disco.rt.menu.*;

import org.joda.time.*;

import java.util.List;

abstract class CalendarRepeatAddState {

   public static class EventType extends EventTypeAdjacencyPair {

      private RepeatingCalendarEntry newEntry;

      public EventType (CalendarStateContext context) {
         this(context, new RepeatingCalendarEntryImpl(null, null, null, null,
               null, null, null, null, null, null, null, null));
      }

      public EventType (CalendarStateContext context,
            RepeatingCalendarEntry newEntry) {
         super(context);
         this.newEntry = newEntry;
         if ( newEntry.getType() != null )
            skipTo(new EventDay(newEntry, getContext()));
      }

      @Override
      public AdjacencyPair nextState (CalendarEntryType type) {
         newEntry.setType(type);
         type.prefill(newEntry);
         return new EventPerson(newEntry, getContext());
      }
   }

   public static class EventPerson extends EventPersonAdjacencyPair {

      private final RepeatingCalendarEntry data;

      public EventPerson (final RepeatingCalendarEntry data,
            final CalendarStateContext context) {
         super(data.getType().getPersonQuestion() != null ? data.getType()
               .getPersonQuestion() : "", context);
         this.data = data;
         if ( data.getType().getPersonQuestion() == null
            || data.getPeople().size() == 0 )
            skipTo(new EventDay(data, getContext()));
      }

      @Override
      public AdjacencyPair nextState (Person person) {
         data.addPerson(person);
         return new EventDay(data, getContext());
      }
   }

   public static class EventDay extends EventDayAdjacencyPair {

      private final RepeatingCalendarEntry data;

      public EventDay (final RepeatingCalendarEntry data,
            final CalendarStateContext context) {
         super("Is the first time this event happens this week", context, data
               .getStart() != null ? CalendarUtils.getDate(data.getStart())
            : new LocalDate());
         this.data = data;
         if ( data.getRepeatStartDate() != null
            && data.getRepeatStartTime() != null && data.getStart() != null
            && data.getRepeat() != null && data.getRepeatEndDate() != null )
            skipTo(new HowLong(data, getContext()));
      }

      @Override
      public AdjacencyPair nextState (LocalDate date) {
         data.setRepeatStartDate(date);
         data.setStart(CalendarUtils.toDateTime(date, data.getStart() != null
            ? CalendarUtils.getTime(data.getStart()) : new LocalTime()));
         return new Frequency(data, getContext());
      }
   }

   public static class Frequency extends CalendarAdjacencyPairImpl {

      public Frequency (final RepeatingCalendarEntry data,
            final CalendarStateContext context) {
         super("How often will it take place", context);
         if ( data.getRepeat() != null )
            skipTo(new HowManyTimes(data, getContext()));
         for (final RepeatingCalendarEntry.Frequency frequency : RepeatingCalendarEntry.Frequency
               .values()) {
            choice(frequency.getDisplayName(), new DialogStateTransition() {

               @Override
               public AdjacencyPair run () {
                  data.setRepeat(frequency);
                  return new HowManyTimes(data, getContext());
               }
            });
         }
         choice("Start Over", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new Cancel(context);
            }
         });
      }
   }

   public static class HowManyTimes extends CalendarAdjacencyPairImpl {

      public HowManyTimes (final RepeatingCalendarEntry entry,
            final CalendarStateContext context) {
         super("How many times will it take place?", context);
         if ( entry.getRepeatEndDate() != null )
            skipTo(new WhenStart(entry, new LocalTime(10, 0), getContext()));
         for (int num = 1; num <= 7; num++) {
            final int fNum = num;
            choice(String.valueOf(num), new DialogStateTransition() {

               @Override
               public AdjacencyPair run () {
                  LocalDate date = entry.getRepeatStartDate();
                  for (int i = 0; i < fNum - 1; ++i)
                     // one less than num because already includes start
                     date = entry.getRepeat().next(date);
                  entry.setRepeatEndDate(date);
                  if(entry.getType().equals(Types.Birthday))
                     return new WhenStartBirthday(entry, getContext());
                  return new WhenStart(entry, new LocalTime(10, 0),
                        getContext());
               }
            });
         }
      }
   }

   private static class WhenStart extends TimeAdjacencyPair {

      private final RepeatingCalendarEntry entry;

      public WhenStart (final RepeatingCalendarEntry entry,
            final LocalTime startTime, final CalendarStateContext context) {
         super("What time does the " + entry.getDisplayTitle() + " start",
               startTime, context);
         this.entry = entry;
         if ( entry.getStart() != null && entry.getRepeatStartTime() != null )
            skipTo(new HowLong(entry, getContext()));
         choice("I made a mistake. Lets start over",
               new DialogStateTransition() {

                  @Override
                  public AdjacencyPair run () {
                     return new Cancel(context);
                  }
               });
      }

      @Override
      public TimeAdjacencyPair changeStartTime (LocalTime time) {
         return new WhenStart(entry, time, getContext());
      }

      @Override
      public AdjacencyPair nextState (LocalTime time) {
         entry.setStart(CalendarUtils.toDateTime(
               CalendarUtils.getDate(entry.getStart()), time));
         entry.setRepeatStartTime(time);
         if(entry.getType().equals(Types.Reminder))
            return new HowLongRemiders(entry, getContext());
         return new HowLong(entry, getContext());
      }
   }
   
   private static class WhenStartBirthday extends CalendarAdjacencyPairImpl {
      public WhenStartBirthday (final RepeatingCalendarEntry entry, final CalendarStateContext context) {
         super("", context);
         LocalTime time = new LocalTime(10, 0);
         entry.setStart(CalendarUtils.toDateTime(
               CalendarUtils.getDate(entry.getStart()), time));
         entry.setRepeatStartTime(time);
         skipTo (new HowLongBirthday(entry, getContext()));
      }
   }
   
   private static class HowLong extends HowLongAdjacencyPair {

      private final RepeatingCalendarEntry data;

      public HowLong (RepeatingCalendarEntry data,
            final CalendarStateContext context) {
         super(context);
         this.data = data;
         if ( data.getDuration() != null && data.getRepeatDuration() != null )
            skipTo(new Where(data, getContext()));
      }

      @Override
      public AdjacencyPair nextState (ReadablePeriod d) {
         data.setDuration(d);
         data.setRepeatDuration(d);
         return new Where(data, getContext());
      }
   }

   private static class HowLongBirthday extends CalendarAdjacencyPairImpl {
      public HowLongBirthday (RepeatingCalendarEntry data, final CalendarStateContext context) {
         super("", context);
         ReadablePeriod d = Minutes.minutes(30);
         data.setDuration(d);
         data.setRepeatDuration(d);
         skipTo(new Where(data, getContext()));
      }
   }
   
   private static class HowLongRemiders extends CalendarAdjacencyPairImpl {
      public HowLongRemiders (RepeatingCalendarEntry data, final CalendarStateContext context) {
         super("", context);
         ReadablePeriod d = Minutes.minutes(1);
         data.setDuration(d);
         data.setRepeatDuration(d);
         skipTo(new Where(data, getContext()));
      }
   }

   private static class Where extends WhereAdjacencyPair {

      private final RepeatingCalendarEntry entry;

      public Where (RepeatingCalendarEntry data, CalendarStateContext context) {
         super(context);
         this.entry = data;
         if ( entry.getPlace() != null )
            skipTo(new Ok(entry, getContext()));
      }

      @Override
      public AdjacencyPair nextState (String place) {
         entry.setPlace(getContext().getPlaceManager().getPlace(place));
         return new Ok(entry, getContext());
      }
   };

   private static class Ok extends CalendarAdjacencyPairImpl {

      private final CalendarEntry data;

      public Ok (CalendarEntry data, final CalendarStateContext context) {
         super("Okay, give me a moment to set up your repeating event", context);
         this.data = data;
      }

      @Override
      public List<String> getChoices () {
         return null;
      }

      @Override
      public AdjacencyPair nextState (String text) {
         return new Thanks(data, getContext());
      }
   };

   private static class Thanks extends CalendarAdjacencyPairImpl {

      private final CalendarEntry data;

      public Thanks (final CalendarEntry data,
            final CalendarStateContext context) {
         super("Okay, here are the entries", context);
         this.data = data;
         choice("Thanks", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(context);
            }
         });
         choice("Oh that's not quite right", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new OkLetsChangeItThen(data, context);
            }
         });
      }

      @Override
      public void enter () {
         getContext().getCalendar().create(data);
         getContext().getUserModel().setProperty(CalendarPlugin.PERFORMED, true);
         getContext().getCalendarUI().showWeek(
               CalendarUtils.getDate(data.getStart()), null, false);
      }
   };

   private static class OkLetsChangeItThen extends CalendarAdjacencyPairImpl {

      public OkLetsChangeItThen (final CalendarEntry data,
            final CalendarStateContext context) {
         super("Lets start over and just change the event", context);
         choice("Ok", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new CalendarChangeState.WhatChange(data, context, true);
            }
         });
      }
   };
}
