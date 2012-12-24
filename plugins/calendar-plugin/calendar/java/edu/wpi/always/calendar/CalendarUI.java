package edu.wpi.always.calendar;

import org.joda.time.LocalDate;

public interface CalendarUI {

   public void showDay (LocalDate day, CalendarUIListener listener,
         boolean touchable);

   public void showWeek (LocalDate startDay, CalendarUIListener listener,
         boolean touchable);

   public void showMonth (LocalDate startDay, CalendarUIListener listener);
}
