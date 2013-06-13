package edu.wpi.always.user.people;

import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;

public interface PeopleManager {

   Person getUser ();

   Person addPerson (String name, Relationship relationship, Gender gender);

   Person getPerson (String name);

   Person[] getPeople ();
   
   Person addPerson(String name, Relationship relationship, Gender gender, String age, String phoneNumber,
		   String SkypeNumber, String Address, Place ZipCode, String spouse);
}
