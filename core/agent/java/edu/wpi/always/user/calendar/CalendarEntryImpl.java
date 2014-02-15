package edu.wpi.always.user.calendar;

import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;
import org.joda.time.*;
import java.util.*;

/**
 * represents an entry in the calendar
 * 
 * @author mwills
 */
public class CalendarEntryImpl implements CalendarEntry {

   private UUID id;
   private CalendarEntryType type;
   private Set<Person> people;
   private Place place;
   private DateTime start;
   private ReadablePeriod duration;

   public CalendarEntryImpl (CalendarEntryType type, Place place,
         DateTime start, ReadablePeriod duration) {
      this(null, type, Collections.<Person> emptySet(), place, start, duration);
   }

   public CalendarEntryImpl (UUID id, CalendarEntryType type,
         Set<Person> people, Place place, DateTime start,
         ReadablePeriod duration) {
      setId(id);
      setType(type);
      this.people = new HashSet<Person>();
      if ( people != null )
         this.people.addAll(people);
      setPlace(place);
      setStart(start);
      setDuration(duration);
   }

   @Override
   public UUID getId () {
      return id;
   }

   @Override
   public void setId (UUID id) {
      this.id = id;
   }

   @Override
   public CalendarEntryType getType () {
      return type;
   }

   @Override
   public void setType (CalendarEntryType type) {
      this.type = type;
   }

   @Override
   public Place getPlace () {
      return place;
   }

   @Override
   public void setPlace (Place place) {
      this.place = place;
   }

   @Override
   public DateTime getStart () {
      return start;
   }

   @Override
   public void setStart (DateTime start) {
      this.start = start;
   }

   @Override
   public ReadablePeriod getDuration () {
      return duration;
   }

   @Override
   public void setDuration (ReadablePeriod duration) {
      this.duration = duration;
   }

   @Override
   public void setTime (Interval interval) {
      start = interval.getStart();
      duration = interval.toPeriod();
   }

   @Override
   public Interval getTime () {
      return new Interval(start, duration);
   }

   @Override
   public Set<Person> getPeople () {
      return people;
   }

   @Override
   public void addPerson (Person p) {
      people.add(p);
   }

   @Override
   public void removePerson (Person p) {
      people.remove(p);
   }

   @Override
   public String getDisplayTitle () {
      return type.getTitle(this);
   }

   @Override
   public CalendarEntry clone () {
      return new CalendarEntryImpl(id, type, people, place, start, duration);
   }

   @Override
   public String toString () {
      return "Calendar Entry [" + getDisplayTitle() + " @ " + getPlace()
         + "], [" + getTime() + "]";
   }

   @Override
   public int hashCode () {
      if ( id == null )
         return 0;
      return id.hashCode();
   }

   @Override
   public boolean equals (Object obj) {
      if ( this == obj )
         return true;
      if ( obj == null )
         return false;
      if ( getClass() != obj.getClass() )
         return false;
      CalendarEntry other = (CalendarEntry) obj;
      if ( id == null || other.getId() == null )
         return false;
      if ( !id.equals(other.getId()) )
         return false;
      return true;
   }
}
