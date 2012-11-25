package edu.wpi.always.cm.ui;

import org.joda.time.*;

public interface CalendarUI {
	
	public void showDay(LocalDate day, CalendarUIListener listener, boolean touchable);
	
	public void showWeek(LocalDate startDay, CalendarUIListener listener, boolean touchable);

	public void showMonth(LocalDate startDay, CalendarUIListener listener);
	
}
