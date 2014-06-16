package edu.wpi.always.calendar.schema;

import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.EventDayAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.HowLongAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.TimeAdjacencyPair;
import edu.wpi.always.calendar.schema.CalendarAdjacencyPairs.WhereAdjacencyPair;
import edu.wpi.always.user.calendar.*;
import edu.wpi.disco.rt.menu.*;
import org.joda.time.*;

abstract class CalendarChangeState {

   public static class EventThisWeek extends CalendarAdjacencyPairImpl {

      private final LocalDate week;

      public EventThisWeek (final CalendarStateContext context,
            final LocalDate week) {
         super("Is the event you want to change this week?", context);
         this.week = week;
         choice("yes, it's this week", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new SelectEvent(context, week);
            }
         });
         choice("no, it's next week", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new SelectEvent(context, week.plusWeeks(1));
            }
         });
         choice("no, it's in the future", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new EventThisWeek(context, week.plusWeeks(2));
            }
         });
         choice("cancel changing this event", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(getContext());
            }
         });
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showWeek(week, this, false);
      }
   }

   private static class SelectEvent extends CalendarAdjacencyPairImpl {

      private final LocalDate week;

      public SelectEvent (final CalendarStateContext context,
            final LocalDate week) {
         super("Please touch the event", context);
         this.week = week;
         choice("Cancel", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(context);
            }
         });
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showWeek(week, this, true);
      }

      @Override
      public AdjacencyPair selected (CalendarEntry entry) {
         if ( getContext().getCalendar().isRepeating(entry) )
            return new UpdateAll(getContext(), entry);
         return new WhatChange(entry, getContext(), false);
      }
   }

   private static class UpdateAll extends CalendarAdjacencyPairImpl {

      public UpdateAll (final CalendarStateContext context,
            final CalendarEntry entry) {
         super("Do you want to change all occurences of the event?", context);
         choice("Yes, change all occurences", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatChange(entry, context, true);
            }
         });
         choice("No, just change the one I selected",
               new DialogStateTransition() {

                  @Override
                  public AdjacencyPair run () {
                     return new WhatChange(entry, context, false);
                  }
               });
      }
   }

   public static class WhatChange extends CalendarAdjacencyPairImpl {

      private final CalendarEntry entry;

      public WhatChange (final CalendarEntry entry,
            final CalendarStateContext context, final boolean updateAll) {
         super("How do you want to change the event?", context);
         this.entry = entry;
         if ( !updateAll ) {
            choice("the date", new DialogStateTransition() {

               @Override
               public AdjacencyPair run () {
                  return new EventDay(entry, context, updateAll);
               }
            });
         }
         choice("the time", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhenStart(entry, CalendarUtils.getTime(entry
                     .getStart()), getContext(), updateAll);
            }
         });
         /* not used since event duration disabled
         choice("the length", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new HowLong(entry, context, updateAll);
            }
         });
         */
         /* event location disabled
         choice("the location", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new Where(entry, context, updateAll);
            }
         });
         */
         choice("I don't want to change anything", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(context);
            }
         });
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showWeek(
               CalendarUtils.getDate(entry.getStart()), null, false);
      }
   }

   public static class EventDay extends EventDayAdjacencyPair {

      private final CalendarEntry entry;
      private final boolean updateAll;

      public EventDay (final CalendarEntry entry,
            final CalendarStateContext context, final boolean updateAll) {
         super("Is the " + entry.getDisplayTitle() + " during the week",
               context, CalendarUtils.getDate(entry.getStart()));
         this.entry = entry;
         this.updateAll = updateAll;
      }

      @Override
      public AdjacencyPair nextState (LocalDate date) {
         if ( updateAll )
            ((RepeatingCalendarEntry) entry).setRepeatStartDate(date);
         entry.setStart(CalendarUtils.toDateTime(date,
               CalendarUtils.getTime(entry.getStart())));
         getContext().getCalendar().update(entry, updateAll);
         return new WhatChange(entry, getContext(), updateAll);
      }
   }

   private static class WhenStart extends TimeAdjacencyPair {

      private final boolean updateAll;
      private final CalendarEntry entry;

      public WhenStart (final CalendarEntry entry, final LocalTime startTime,
            final CalendarStateContext context, final boolean updateAll) {
         super("What time does the " + entry.getDisplayTitle() + " start",
               startTime, context);
         this.entry = entry;
         this.updateAll = updateAll;
      }

      @Override
      public TimeAdjacencyPair changeStartTime (LocalTime time) {
         return new WhenStart(entry, time, getContext(), updateAll);
      }

      @Override
      public AdjacencyPair nextState (LocalTime time) {
         if ( updateAll ) {
            ((RepeatingCalendarEntry) entry).setRepeatStartTime(time);
         } else {
            entry.setStart(CalendarUtils.toDateTime(
                  CalendarUtils.getDate(entry.getStart()), time));
         }
         getContext().getCalendar().update(entry, updateAll);
         return new WhatChange(entry, getContext(), updateAll);
      }
   }
   
   // NB: Not used since event duration disabled
   @SuppressWarnings("unused")
   private static class HowLong extends HowLongAdjacencyPair {

      private final boolean updateAll;
      private final CalendarEntry entry;

      public HowLong (CalendarEntry entry, final CalendarStateContext context,
            boolean updateAll) {
         super(context);
         this.entry = entry;
         this.updateAll = updateAll;
      }

      @Override
      public AdjacencyPair nextState (ReadablePeriod d) {
         if ( updateAll ) {
            ((RepeatingCalendarEntry) entry).setRepeatDuration(d);
         } else {
            entry.setDuration(d);
         }
         getContext().getCalendar().update(entry, updateAll);
         return new WhatChange(entry, getContext(), updateAll);
      }
   }

   // NB: This state no longer used since event location disabled
   @SuppressWarnings("unused")
   private static class Where extends WhereAdjacencyPair {

      private final CalendarEntry entry;
      private final boolean updateAll;

      public Where (CalendarEntry entry, CalendarStateContext context,
            boolean updateAll) {
         super(context);
         this.entry = entry;
         this.updateAll = updateAll;
      }

      @Override
      public AdjacencyPair nextState (String place) {
         entry.setPlace(getContext().getPlaceManager().getPlace(place));
         getContext().getCalendar().update(entry, updateAll);
         return new WhatChange(entry, getContext(), updateAll);
      }
   };
}
