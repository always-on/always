package edu.wpi.always.calendar;

import edu.wpi.always.user.calendar.CalendarEntry;
import org.joda.time.LocalDate;

public interface CalendarUIListener {

   public void entrySelected (CalendarEntry entry);

   public void daySelected (LocalDate dateMidnight);
}
