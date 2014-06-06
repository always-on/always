package edu.wpi.always.calendar.schema;

import edu.wpi.disco.rt.menu.*;
import org.joda.time.LocalDate;

public class WhatDo extends CalendarAdjacencyPairImpl {

   public WhatDo (final CalendarStateContext context) {
      super("How do you want to use the calendar?", context);
      choice("Add a new event", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            // NB: Repeating events disabled
            // new RepeatEvent(context);
            return new CalendarSingleAddState.EventType(context);
         }
      });
      choice("Change an event", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new CalendarChangeState.EventThisWeek(context,
                  new LocalDate());
         }
      });
      choice("Delete an event", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new CalendarDeleteState.EventThisWeek(context,
                  new LocalDate());
         }
      });
      choice("Just look at the calendar", new DialogStateTransition() {

         @Override
         public AdjacencyPair run () {
            return new CalendarViewState.LookCalendarStyle(context);
         }
      });
   }
   
   @Override
   public void enter () {
      getContext().getCalendarUI().showWeek(new LocalDate(), this, false);
   }

   // NB: This state no longer used, as repeating events disabled
   public static class RepeatEvent extends CalendarAdjacencyPairImpl {

      public RepeatEvent (final CalendarStateContext context) {
         super("ok, now, is the event going to repeat?", context);
         choice("Yes, the event will repeat", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new CalendarRepeatAddState.EventType(context);
            }
         });
         choice("No, the event will not repeat", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new CalendarSingleAddState.EventType(context);
            }
         });
      }
   }
}
