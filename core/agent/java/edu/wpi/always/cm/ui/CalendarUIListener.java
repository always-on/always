package edu.wpi.always.cm.ui;

import org.joda.time.*;

import edu.wpi.always.user.calendar.*;

public interface CalendarUIListener {
	public void entrySelected(CalendarEntry entry);

	public void daySelected(LocalDate dateMidnight);
}
