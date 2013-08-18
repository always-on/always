<<<<<<< HEAD
package edu.wpi.always.user.people;

import org.joda.time.MonthDay;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;

/**
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link edu.wpi.always.user.UserModel#INHIBIT_SAVE}. @author rich
 */
public interface PeopleManager {

   UserModel getUserModel();

   Person getUser ();

   Person addPerson (String name);

   Person getPerson (String name);

   Person[] getPeople ();

   Person addPerson(String name, Relationship relationship, Gender gender, int age, String phone,
         String skype, Place place, Person spouse, MonthDay birthday);
}
=======
package edu.wpi.always.user.people;

import org.joda.time.MonthDay;

import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;

/**
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link edu.wpi.always.user.UserModel#INHIBIT_SAVE}. @author rich
 */
public interface PeopleManager {

   UserModel getUserModel();

   Person getUser ();

   Person addPerson (String name);

   Person getPerson (String name);

   Person[] getPeople (boolean includeUser);

   Person addPerson(String name, Relationship relationship, Gender gender, int age, String phone,
         String skype, Place place, Person spouse, MonthDay birthday);
}
>>>>>>> upstream/develop
