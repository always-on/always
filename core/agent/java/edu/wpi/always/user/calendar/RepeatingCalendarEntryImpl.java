package edu.wpi.always.user.calendar;

import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import org.joda.time.*;
import java.util.*;

public class RepeatingCalendarEntryImpl extends CalendarEntryImpl implements
      RepeatingCalendarEntry {

   private UUID repeatingId;
   private LocalDate startDate;
   private LocalDate endDate;
   private LocalTime startTime;
   private ReadablePeriod duration;
   private Frequency frequency;

   public RepeatingCalendarEntryImpl (UUID id, CalendarEntryType type,
         Set<Person> people, Place place, DateTime instanceStart,
         ReadablePeriod instanceDuration, UUID repeatingId,
         LocalDate startDate, LocalDate endDate, LocalTime startTime,
         ReadablePeriod duration, Frequency frequency) {
      super(id, type, people, place, instanceStart, instanceDuration);
      this.repeatingId = repeatingId;
      this.startDate = startDate;
      this.endDate = endDate;
      this.startTime = startTime;
      this.duration = duration;
      this.frequency = frequency;
   }

   @Override
   public RepeatingCalendarEntry clone () {
      return new RepeatingCalendarEntryImpl(getId(), getType(), getPeople(),
            getPlace(), getStart(), getRepeatDuration(), repeatingId,
            startDate, endDate, startTime, duration, frequency);
   }

   @Override
   public UUID getRepeatId () {
      return repeatingId;
   }

   @Override
   public void setRepeatId (UUID repeatingId) {
      this.repeatingId = repeatingId;
   }

   @Override
   public LocalDate getRepeatStartDate () {
      return startDate;
   }

   @Override
   public void setRepeatStartDate (LocalDate date) {
      startDate = date;
   }

   @Override
   public LocalDate getRepeatEndDate () {
      return endDate;
   }

   @Override
   public void setRepeatEndDate (LocalDate date) {
      endDate = date;
   }

   @Override
   public LocalTime getRepeatStartTime () {
      return startTime;
   }

   @Override
   public void setRepeatStartTime (LocalTime time) {
      startTime = time;
   }

   @Override
   public ReadablePeriod getRepeatDuration () {
      return duration;
   }

   @Override
   public void setRepeatDuration (ReadablePeriod duration) {
      this.duration = duration;
   }

   @Override
   public Frequency getRepeat () {
      return frequency;
   }

   @Override
   public void setRepeat (Frequency frequency) {
      this.frequency = frequency;
   }

   @Override
   public Iterable<LocalDate> getRepeatDateIterator () {
      return new RepeatDateIterable();
   }

   public class RepeatDateIterable implements Iterable<LocalDate> {

      @Override
      public Iterator<LocalDate> iterator () {
         return new Iterator<LocalDate>() {

            private LocalDate nextDate = getRepeatStartDate();

            @Override
            public boolean hasNext () {
               return !nextDate.isAfter(getRepeatEndDate());
            }

            @Override
            public LocalDate next () {
               LocalDate currentDate = nextDate;
               nextDate = getRepeat().next(nextDate);
               return currentDate;
            }

            @Override
            public void remove () {
               throw new UnsupportedOperationException(
                     "remove is not supported");
            }
         };
      }
   }
}
