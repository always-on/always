package edu.wpi.always.calendar.schema;

import edu.wpi.disco.rt.menu.*;
import org.joda.time.LocalDate;

abstract class CalendarViewState {

 
   public static class LookCalendarStyle extends CalendarAdjacencyPairImpl {

      public LookCalendarStyle (final CalendarStateContext context) {
      
         super(
               "Ok, What would you like to look <emphasis level=\"strong\">at</emphasis>",
               context);
         
         choice("This week", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WeekView(context, new LocalDate());
            }
         });
         choice("Next week", new DialogStateTransition() {
            
            @Override
            public AdjacencyPair run () {
               return new WeekView(context, new LocalDate().plusWeeks(1));
            }
           
         });
         choice("The whole month", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new MonthView(context, new LocalDate());
            }
         });
      }
   };

   private static class MonthView extends LookCalendar {

      private final LocalDate month;

      public MonthView (final CalendarStateContext context,
            final LocalDate month) {
         super(context);
         this.month = month;
         choice("The next month", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new MonthView(context, month.plusMonths(1));
            }
         });
         choice("The previous month", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new MonthView(context, month.minusMonths(1));
            }
         });
         addOtherChoices(context);
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showMonth(month, this);
      }

      @Override
      public AdjacencyPair selected (LocalDate date) {
         return new DayView(getContext(), date);
      }
   }

   private static class WeekView extends LookCalendar {

      private final LocalDate week;

      public WeekView (final CalendarStateContext context, final LocalDate week) {
         super(context);
         this.week = week;
         choice("The next week", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WeekView(context, week.plusWeeks(1));
            }
         });
         choice("The previous week", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WeekView(context, week.minusWeeks(1));
            }
         });
         addOtherChoices(context);
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showWeek(week, this, false);
      }

      @Override
      public AdjacencyPair selected (LocalDate date) {
         return new DayView(getContext(), date);
      }
   }

   private static class DayView extends LookCalendar {

      private final LocalDate day;

      public DayView (final CalendarStateContext context, final LocalDate day) {
         super(context);
         this.day = day;
         choice("The next day", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new DayView(context, day.plusDays(1));
            }
         });
         choice("The previous day", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new DayView(context, day.minusDays(1));
            }
         });
         addOtherChoices(context);
      }

      @Override
      public void enter () {
         getContext().getCalendarUI().showDay(day, this, false);
      }
   }

   public static abstract class LookCalendar extends CalendarAdjacencyPairImpl {

      public LookCalendar (final CalendarStateContext context) {
         super("When you are done looking touch done below", context);
      }

      protected void addOtherChoices (final CalendarStateContext context) {
         choice("Show me another view", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new LookCalendarStyle(context);
            }
         });
         choice("Done", new DialogStateTransition() {

            @Override
            public AdjacencyPair run () {
               return new WhatDo(context);
            }
         });
      }
   };
}
