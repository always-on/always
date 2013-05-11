package edu.wpi.always.user.people;

import edu.wpi.always.user.places.Place;
import org.joda.time.MonthDay;

/* 
 * This interface defines accessors for the core properties of a person.
 */
public interface Person {

   enum Gender {
      Male, Female;
   }

   enum Relationship {
      Friend, Parent, Father, Mother, Spouse, Husband, Wife, Offspring, Son, Daughter, Sibling, Brother, Sister, Grandchild, Grandson, Granddaughter, Grandparent, Grandfather, Grandmother;
   }

   String getName ();

   Gender getGender ();
   
   void setGender (Person.Gender gender);

   MonthDay getBirthday ();
   
   void setBirthday (MonthDay day);

   Place getLocation ();

   void setLocation (Place place);

   String getPhoneNumber ();
   
   void setPhoneNumber (String number);

   /**
    * @return null if no related persons
    */
   Person[] getRelated (Relationship relationship);
   
   void addRelated (Person otherPerson, Relationship relationship);

}
