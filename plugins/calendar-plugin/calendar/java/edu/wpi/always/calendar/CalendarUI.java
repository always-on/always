package edu.wpi.always.calendar;

import org.joda.time.LocalDate;

public interface CalendarUI {

   void showDay (LocalDate day, CalendarUIListener listener,
         boolean touchable);

   void showWeek (LocalDate startDay, CalendarUIListener listener,
         boolean touchable);

   void showMonth (LocalDate startDay, CalendarUIListener listener);
   
}
