package edu.wpi.always.user.people;

import edu.wpi.always.user.places.Place;
import org.joda.time.MonthDay;

public interface Person {

   enum Gender {
      Male, Female;
   }

   enum Relationship {
      Friend, Parent, Father, Mother, Spouse, Husband, Wife, Offspring, Son, Daughter, Sibling, Brother, Sister, Grandchild, Grandson, Granddaughter, Grandparent, Grandfather, Grandmother;
   }

   String getName ();

   void addRelationship (Person otherPerson, Relationship relationship);

   void setGender (Person.Gender gender);

   void setBirthday (MonthDay day);

   MonthDay getBirthday ();

   Place getLocation ();

   void setLocation (Place place);

   void setPhoneNumber (String number);

   String getPhoneNumber ();

   Gender getGender ();
}
