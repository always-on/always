package edu.wpi.always.user.calendar;

import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadablePeriod;

import edu.wpi.always.user.people.Person;
import edu.wpi.always.user.places.Place;

public interface CalendarEntry extends Cloneable {

   public UUID getId ();

   void setId (UUID randomUUID);

   public String getDisplayTitle ();

   public CalendarEntryType getType ();

   public void setType (CalendarEntryType type);

   public Set<Person> getPeople ();

   public void addPerson (Person p);

   public void removePerson (Person p);

   public Place getPlace ();

   public void setPlace (Place place);

   public void setStart (DateTime start);

   public DateTime getStart ();

   public void setDuration (ReadablePeriod duration);

   public ReadablePeriod getDuration ();

   public void setTime (Interval interval);

   public Interval getTime ();

   public CalendarEntry clone ();
}
