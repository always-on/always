package edu.wpi.always.calendar.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.wpi.always.user.calendar.AbstractCalendar;
import edu.wpi.always.user.calendar.CalendarEntry;

/**
 * A sample implementation of a simple calendar api for the Always On Project
 * 
 * @author mwills
 * 
 * 
 */
public class ListCalendar extends AbstractCalendar{

	private final List<CalendarEntry> entries = new ArrayList<CalendarEntry>();

	@Override
	public Iterator<CalendarEntry> iterator() {
		return entries.iterator();
	}

	@Override
	public void addEntry(CalendarEntry entry) {
		entries.add(entry.clone());
	}

}
