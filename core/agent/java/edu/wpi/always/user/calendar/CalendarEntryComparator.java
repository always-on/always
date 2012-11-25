package edu.wpi.always.user.calendar;

import java.util.Comparator;

/**
 * A comparator that compares calendar entries by their start time
 * 
 * @author mwills
 *
 */
public class CalendarEntryComparator implements Comparator<CalendarEntry> {

	@Override
	public int compare(CalendarEntry e1, CalendarEntry e2) {
		return e1.getStart().compareTo(e2.getStart());
	}

}
