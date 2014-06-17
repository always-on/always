package edu.wpi.always.calendar.schema;

import edu.wpi.always.user.calendar.CalendarEntry;
import edu.wpi.disco.rt.menu.*;
import org.joda.time.LocalDate;
import org.joda.time.format.*;

abstract class CalendarDeleteState {

   public static class EventThisWeek extends CalendarAdjacencyPairImpl {

      private final LocalDate week;

      public EventThisWeek (final CalendarStateContext context,
            final LocalDate week) {
         super("Is the event you want to delete this week?", context);
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
         choice("let's not delete anything", new DialogStateTransition() {

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
         if ( getContext().getCalendar().isRepeating(entry) ) {
            getContext().getCalendarUI().showWeek(week, null, false);
            return new DeleteAll(getContext(), entry, week);
         }
         getContext().getCalendar().delete(entry, false);
         getContext().getCalendarUI().showWeek(week, null, false);
         return new EventDeleted(getContext());
      }
   }

   private static class DeleteAll extends CalendarAdjacencyPairImpl {

      public DeleteAll (final CalendarStateContext context,
            final CalendarEntry entry, final LocalDate week) {
         super("Do you want to delete all occurences of this event", context);
         choice("Yes delete all of them", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               getContext().getCalendarUI().showWeek(week, null, false);
               return new ConfirmDelete(context, entry, week, true);
            }
         });
         choice("No just this one", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               getContext().getCalendarUI().showWeek(week, null, false);
               return new ConfirmDelete(context, entry, week, false);
            }
         });
      }
   }

   private static final DateTimeFormatter CONFIRM_DELETE_DATE_FORMAT = DateTimeFormat
         .forPattern("");

   private static class ConfirmDelete extends CalendarAdjacencyPairImpl {

      public ConfirmDelete (final CalendarStateContext context,
            final CalendarEntry entry, final LocalDate week,
            final boolean deleteAll) {
         super("Are you sure you want to delete " + entry.getDisplayTitle()
            + " on " + CONFIRM_DELETE_DATE_FORMAT.print(entry.getStart()),
               context);
         choice("Yes", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               getContext().getCalendar().delete(entry, deleteAll);
               getContext().getCalendarUI().showWeek(week, null, false);
               return new EventDeleted(context);
            }
         });
         choice("No", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               getContext().getCalendarUI().showWeek(week, null, false);
               return new DidntDeleteEvent(context);
            }
         });
      }
   }

   private static class EventDeleted extends CalendarAdjacencyPairImpl {

      public EventDeleted (final CalendarStateContext context) {
         super("Okay, I removed the event from your calendar", context);
      }

      @Override
      public AdjacencyPair nextState (String text) {
         return new WhatDo(getContext());
      }
   }

   private static class DidntDeleteEvent extends CalendarAdjacencyPairImpl {

      public DidntDeleteEvent (final CalendarStateContext context) {
         super("Okay, Lets start over", context);
         choice("Ok", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(getContext());
            }
         });
      }
   }
}