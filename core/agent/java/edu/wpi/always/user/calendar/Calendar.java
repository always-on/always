package edu.wpi.always.user.calendar;

import org.joda.time.*;
import java.util.*;

public interface Calendar {

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
