package edu.wpi.always.user.calendar;


public interface CalendarEntryType {

	String getTitle(CalendarEntry entry);

	String getDisplayName();
	String getId();

	String getPersonQuestion();

	void prefill(RepeatingCalendarEntry newEntry);

	void prefill(CalendarEntry newEntry);

}
