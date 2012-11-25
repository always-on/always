package edu.wpi.always.user.calendar;


/**
 * Represents a conflict that was detected during an update operation
 *
 * @author mwills
 * 
 */
public class Conflict {
	private final CalendarEntry entry1;
	private final CalendarEntry entry2;
	/**
	 * Create a new conflict object
	 * @param entry1 
	 * @param entry2 
	 */
	public Conflict(CalendarEntry entry1, CalendarEntry entry2) {
		this.entry1 = entry1;
		this.entry2 = entry2;
	}
	/**
	 * @return one of the entries that is in conflict
	 */
	public CalendarEntry getEntry1(){
		return entry1;
	}
	/**
	 * @return one of the entries that is in conflict
	 */
	public CalendarEntry getEntry2(){
		return entry2;
	}

}
