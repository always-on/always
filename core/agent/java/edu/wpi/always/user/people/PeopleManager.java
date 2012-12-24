package edu.wpi.always.user.people;

import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;

public interface PeopleManager {

   public Person getUser ();

   public Person addPerson (String name, Relationship relationship,
         Gender gender);

   public Person getPerson (String name);

   public Person[] getPeople ();
}
