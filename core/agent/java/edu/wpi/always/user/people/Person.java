package edu.wpi.always.user.people;

import org.joda.time.MonthDay;
import edu.wpi.always.user.places.Place;

/** 
 * This interface defines accessors for the core properties of a person.
 * 
 * Note user model is automatically saved to file after every update command
 * unless prevented with {@link edu.wpi.always.user.UserModelBase#INHIBIT_SAVE}.
 */
public interface Person {

   enum Gender {
      Male, Female;
   }

   // note ordered from most to least specific to improve performance of getRelationship()
   enum Relationship {
         Father, Mother, Parent,
         Husband, Wife, Spouse,
         Son, Daughter, Offspring,
         Brother, Sister, Sibling,
         Grandson, Granddaughter,  Grandchild,
         Grandfather, Grandmother, Grandparent,
         Aunt, Uncle, Cousin, Friend
   };

   public static String[] Month = { "January", "February", "March", "April",
      "May", "June", "July", "August", "September", "October", "November",
      "December" };

   String getName ();

   void setName (String name);

   Gender getGender ();

   void setGender (Gender gender);

   MonthDay getBirthday ();

   void setBirthday (MonthDay day);

   String getSkypeNumber ();

   void setSkypeNumber (String number);

   /**
    * Relationship to the user (if more than one, chosen randomly).
    */
   Relationship getRelationship ();

   /**
    * Relationship to the user
    */
   void setRelationship (Relationship relationship);

   int getAge ();

   void setAge (int age);

   Place getLocation ();

   void setLocation (Place place);

   /**
    * @return null if no related persons
    */
   Person[] getRelated (Relationship relationship);

   void addRelated (Person otherPerson, Relationship relationship);
   
   Person getSpouse ();

   // the following are specific to the plugin for talking about people
   // but need to be here because can be applied to any person
   
   enum AboutStatus { Positive, Negative }
   
   AboutStatus getAboutStatus ();
   
   String getAboutComment ();
   
   // to batch single file save
   void setAboutStatusComment (AboutStatus status, String comment);

}
