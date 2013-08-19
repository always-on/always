package edu.wpi.always.user.calendar;

import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

/**
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link edu.wpi.always.user.UserModel#INHIBIT_SAVE}.
 *
 */
public interface Calendar extends Iterable<CalendarEntry> {

   /**
    * Insert a new event into the system
    * 
    * @param entry The Calendar Entry to be added to the calendar
    * @return a conflict if any that was detected while inserting the event
    */
   public List<Conflict> create (CalendarEntry entry);

   /**
    * Try inserting a new event into the system, but don't actually
    * 
    * @param entry The Calendar Entry to be added to the calendar
    * @return a conflict if any that was detected while inserting the event
    */
   public List<Conflict> dryCreate (CalendarEntry entry);

   /**
    * retrieve the entries between two times
    * 
    * @param start the beginning of the time period to search
    * @param end the end of the time period to search
    * @return a list of entries sorted by start time
    */
   public List<CalendarEntry> retrieve (ReadableInstant start,
         ReadableInstant end);

   /**
    * retrieve the entries over a time interval
    * 
    * @param searchInterval the interval to search, if the interval is null all
    *           events will be returned
    * @return a list of entries sorted by start time
    */
   public List<CalendarEntry> retrieve (Interval searchInterval);

   public CalendarEntry retrieveById (UUID id);

   public List<RepeatingCalendarEntry> retrieveByRepeatId (UUID id);

   /**
    * Update a calendar entry. Calendar entries that share the same id will be
    * updated. If no entries exist the given entry will be inserted into the
    * calendar
    * 
    * @param entry the entry to be updated
    * @return any conflicts that occurred while updating
    */
   public List<Conflict> update (CalendarEntry entry, boolean updateAll);

   /**
    * Try updating a calendar entry, but don't actually. Calendar entries that
    * share the same id will be updated. If no entries exist the given entry
    * would be inserted into the calendar
    * 
    * @param entry the entry to be updated
    * @return any conflicts that occurred while updating
    */
   public List<Conflict> dryUpdate (CalendarEntry entry, boolean updateAll);

   /**
    * Delete the given entry
    * 
    * @param entry the entry to be deleted
    * @return the most recent version of the event upon deletion
    */
   public List<CalendarEntry> delete (CalendarEntry entry, boolean updateAll);

   public boolean isRepeating (CalendarEntry entry);

   /**
    * @return the current time
    */
   public DateTime now ();
}
