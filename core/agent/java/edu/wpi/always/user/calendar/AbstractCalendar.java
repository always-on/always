package edu.wpi.always.user.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;

public abstract class AbstractCalendar implements Calendar {

   public abstract void addEntry (CalendarEntry entry);

   @Override
   public List<Conflict> create (CalendarEntry entry) {
      entry.setId(UUID.randomUUID());
      return update(entry, true);
   }

   @Override
   public List<Conflict> dryCreate (CalendarEntry entry) {
      CalendarEntry tmp = entry.clone();
      tmp.setId(UUID.randomUUID());
      return dryUpdate(tmp, true);
   }

   @Override
   public List<CalendarEntry> retrieve (ReadableInstant start,
         ReadableInstant end) {
      return retrieve(new Interval(start, end));
   }

   @Override
   public synchronized List<CalendarEntry> retrieve (Interval searchInterval) {
      List<CalendarEntry> result = new ArrayList<CalendarEntry>();
      for (CalendarEntry entry : this) {
         if ( searchInterval == null
            || searchInterval.overlaps(entry.getTime()) )
            result.add(entry.clone());
      }
      Collections.sort(result, new CalendarEntryComparator());
      return result;
   }

   @Override
   public CalendarEntry retrieveById (UUID id) {
      for (CalendarEntry entry : this) {
         if ( entry.getId().equals(id) )
            return entry;
      }
      return null;
   }

   @Override
   public List<RepeatingCalendarEntry> retrieveByRepeatId (UUID id) {
      List<RepeatingCalendarEntry> entries = new ArrayList<RepeatingCalendarEntry>();
      Iterator<CalendarEntry> entriesIterator = iterator();
      while (entriesIterator.hasNext()) {
         CalendarEntry currentEntry = entriesIterator.next();
         if ( currentEntry instanceof RepeatingCalendarEntry
            && ((RepeatingCalendarEntry) currentEntry).getRepeatId().equals(id) ) {
            entries.add((RepeatingCalendarEntry) currentEntry);
         }
      }
      return entries;
   }

   private List<Conflict> getConflict (CalendarEntry entry) {
      List<Conflict> conflicts = new ArrayList<Conflict>();
      List<CalendarEntry> entries = retrieve(entry.getTime());
      for (CalendarEntry conflictingEntry : entries) {
         if ( !conflictingEntry.getId().equals(entry.getId()) )
            conflicts.add(new Conflict(entry, conflictingEntry));
      }
      return conflicts;
   }

   private boolean isRepeatingException (RepeatingCalendarEntry entry) {
      if ( !CalendarUtils.getTime(entry.getStart()).equals(
            entry.getRepeatStartTime()) )
         return true;
      if ( !entry.getDuration().toPeriod()
            .equals(entry.getRepeatDuration().toPeriod()) )
         return true;
      boolean foundDate = false;
      for (LocalDate currentDate : entry.getRepeatDateIterator()) {
         if ( CalendarUtils.getDate(entry.getStart()).equals(currentDate) )
            foundDate = true;
      }
      if ( !foundDate )
         return true;
      return false;
   }

   private synchronized List<Conflict> update (CalendarEntry entry,
         boolean updateAll, boolean effectChange) {
      if ( entry instanceof RepeatingCalendarEntry && updateAll ) {
         RepeatingCalendarEntry repeatingEntry = (RepeatingCalendarEntry) entry;
         if ( !effectChange )
            repeatingEntry = repeatingEntry.clone();
         if ( repeatingEntry.getRepeatId() == null )
            repeatingEntry.setRepeatId(UUID.randomUUID());
         List<RepeatingCalendarEntry> storedEntries = retrieveByRepeatId(repeatingEntry
               .getRepeatId());
         List<RepeatingCalendarEntry> exceptionEntries = new ArrayList<RepeatingCalendarEntry>();
         List<LocalDate> deletedEventDates = new ArrayList<LocalDate>();
         Map<LocalDate, UUID> storedUUIDs = new HashMap<LocalDate, UUID>();
         if ( storedEntries.size() > 0 ) {
            // get an old entry to extract repeat data from
            RepeatingCalendarEntry oldEntry = storedEntries.get(0);
            // calculate dates where an occurrence was deleted
            for (LocalDate currentDate : oldEntry.getRepeatDateIterator()) {
               boolean found = false;
               for (RepeatingCalendarEntry storedEntry : storedEntries)
                  // all elements of stored entry should be valid
                  if ( CalendarUtils.getDate(storedEntry.getStart()).equals(
                        currentDate) )
                     found = true;
               if ( !found )
                  deletedEventDates.add(currentDate);
            }
            // remove old entry
            Iterator<RepeatingCalendarEntry> storedIterator = storedEntries
                  .iterator();
            while (storedIterator.hasNext()) {
               if ( storedIterator.next().getId()
                     .equals(repeatingEntry.getId()) )
                  storedIterator.remove();
            }
            // extract exceptions to the rule
            storedIterator = storedEntries.iterator();
            while (storedIterator.hasNext()) {
               RepeatingCalendarEntry storedEntry = storedIterator.next();
               if ( isRepeatingException(storedEntry) ) {
                  exceptionEntries.add(storedEntry);
                  storedIterator.remove();
               }
            }
            // create map of stored UUIDs
            for (RepeatingCalendarEntry storedEntry : storedEntries)
               storedUUIDs.put(CalendarUtils.getDate(storedEntry.getStart()),
                     storedEntry.getId());
         }
         // calculate all of the entries for the updated event
         List<RepeatingCalendarEntry> newEntries = new ArrayList<RepeatingCalendarEntry>();
         for (RepeatingCalendarEntry oldEntry : exceptionEntries)
            newEntries.add(new RepeatingCalendarEntryImpl(oldEntry.getId(),
                  repeatingEntry.getType(), repeatingEntry.getPeople(),
                  repeatingEntry.getPlace(), oldEntry.getStart(), oldEntry
                        .getDuration(), repeatingEntry.getRepeatId(),
                  repeatingEntry.getRepeatStartDate(), repeatingEntry
                        .getRepeatEndDate(), repeatingEntry
                        .getRepeatStartTime(), repeatingEntry
                        .getRepeatDuration(), repeatingEntry.getRepeat()));
         for (LocalDate date : repeatingEntry.getRepeatDateIterator()) {
            if ( !deletedEventDates.contains(date) ) {
               UUID id = storedUUIDs.get(date);
               if ( id == null )
                  id = UUID.randomUUID();
               newEntries.add(new RepeatingCalendarEntryImpl(id, repeatingEntry
                     .getType(), repeatingEntry.getPeople(), repeatingEntry
                     .getPlace(), CalendarUtils.toDateTime(date,
                     repeatingEntry.getRepeatStartTime()), repeatingEntry
                     .getRepeatDuration(), repeatingEntry.getRepeatId(),
                     repeatingEntry.getRepeatStartDate(), repeatingEntry
                           .getRepeatEndDate(), repeatingEntry
                           .getRepeatStartTime(), repeatingEntry
                           .getRepeatDuration(), repeatingEntry.getRepeat()));
            }
         }
         // make sure that the input entry has the same id as the one in the
         // calendar (such as if you get a new event
         for (RepeatingCalendarEntry newEntry : newEntries)
            if ( CalendarUtils.getDate(repeatingEntry.getStart()).equals(
                  CalendarUtils.getDate(newEntry.getStart())) )
               repeatingEntry.setId(newEntry.getId());
         // calculate conflicts
         List<Conflict> conflicts = new ArrayList<Conflict>();
         for (RepeatingCalendarEntry newEntry : newEntries)
            conflicts.addAll(getConflict(newEntry));
         // NOTE: any exception to the rule will persist even if is outside of
         // the new rule
         if ( effectChange ) {
            delete(entry, true);
            for (RepeatingCalendarEntry newEntry : newEntries) {
               addEntry(newEntry);
            }
         }
         return conflicts;
      } else {
         if ( effectChange ) {
            delete(entry, false);
            addEntry(entry);
         }
         return getConflict(entry);
      }
   }

   /**
    * Update a calendar entry. Calendar entries that share the same id will be
    * updated. If no entries exist the given entry will be inserted into the
    * calendar
    * 
    * @param entry the entry to be updated
    * @return any conflicts that occurred while updating
    */
   @Override
   public synchronized List<Conflict> update (CalendarEntry entry,
         boolean updateAll) {
      return update(entry, updateAll, true);
   }

   /**
    * Try updating a calendar entry, but don't actually. Calendar entries that
    * share the same id will be updated. If no entries exist the given entry
    * would be inserted into the calendar
    * 
    * @param entry the entry to be updated
    * @return any conflicts that occurred while updating
    */
   @Override
   public synchronized List<Conflict> dryUpdate (CalendarEntry entry,
         boolean updateAll) {
      return update(entry, updateAll, false);
   }

   /**
    * Delete the given entry
    * 
    * @param entry the entry to be deleted
    * @return the most recent version of the event upon deletion
    */
   @Override
   public synchronized List<CalendarEntry> delete (CalendarEntry entry,
         boolean updateAll) {
      if ( entry instanceof RepeatingCalendarEntry && updateAll ) {
         UUID targetId = ((RepeatingCalendarEntry) entry).getRepeatId();
         List<CalendarEntry> deletedEntries = new ArrayList<CalendarEntry>();
         Iterator<CalendarEntry> entriesIterator = iterator();
         while (entriesIterator.hasNext()) {
            CalendarEntry currentEntry;
            currentEntry = entriesIterator.next();
            if ( currentEntry instanceof RepeatingCalendarEntry
               && ((RepeatingCalendarEntry) currentEntry).getRepeatId().equals(
                     targetId) ) {
               deletedEntries.add(currentEntry);
               entriesIterator.remove();
            }
         }
         return deletedEntries;
      } else {
         CalendarEntry deletedEntry = null;
         Iterator<CalendarEntry> entriesIterator = iterator();
         while (entriesIterator.hasNext()) {
            CalendarEntry currentEntry;
            currentEntry = entriesIterator.next();
            if ( currentEntry.getId().equals(entry.getId()) ) {
               deletedEntry = currentEntry;
               entriesIterator.remove();
            }
         }
         return Collections.singletonList(deletedEntry);
      }
   }

   @Override
   public boolean isRepeating (CalendarEntry entry) {
      return entry instanceof RepeatingCalendarEntry;
   }

   /**
    * @return the current time
    */
   @Override
   public DateTime now () {
      return DateTime.now();
   }
}
