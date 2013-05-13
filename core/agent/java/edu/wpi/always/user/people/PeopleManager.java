package edu.wpi.always.user.people;

import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;

public interface PeopleManager {

   Person getUser ();

   Person addPerson (String name, Relationship relationship, Gender gender);

   Person getPerson (String name);

   Person[] getPeople ();
}
