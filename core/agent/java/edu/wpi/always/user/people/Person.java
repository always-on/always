package edu.wpi.always.user.people;

import edu.wpi.always.user.places.Place;
import edu.wpi.always.user.places.ZipCodes.ZipCodeEntry;

import org.joda.time.MonthDay;

/* 
 * This interface defines accessors for the core properties of a person.
 */
public interface Person {

   enum Gender {
      Male, Female;
   }

   enum Relationship {
      Friend, Parent, Father, Mother, Spouse, Husband, Wife, Offspring, Son, Daughter, Sibling, Brother, Sister, Grandchild, Grandson, Granddaughter, Grandparent, Grandfather, Grandmother, Aunt, Uncle, Cousin;
   }

   String getName ();

   Gender getGender ();
   
   void setGender (Person.Gender gender);

   MonthDay getBirthday ();
   
   void setBirthday (MonthDay day);

   String getZipCode ();

   void setZipCode (String ZipCode);

   String getPhoneNumber ();
   
   void setPhoneNumber (String number);
   
   String getSkypeNumber ();
   
   void setSkypeNumber(String number);
  
   String getRelationship();
   
   void setRelationship(Relationship relationship);
   
   String getSpouse();
   
   void setSpouse(String spouse);
   
   String getAge();
   
   void setAge(String age);
   
   String getAddress();

   void setAddress(String address);
   
   Place getLocation ();

   void setLocation (Place place);

   /**
    * @return null if no related persons
    */
   Person[] getRelated (Relationship relationship);
   
   void addRelated (Person otherPerson, Relationship relationship);

}
