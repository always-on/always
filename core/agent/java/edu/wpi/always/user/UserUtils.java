package edu.wpi.always.user;

import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;
import org.joda.time.MonthDay;
import java.io.*;

public abstract class UserUtils {

   public static final File userHomeDir = new File(System.getProperty("user.home"));
   
   public static final File userProgramDataRoot = new File(userHomeDir, "AlwaysOn");
   
   // TODO Use DropBox folder or give warning and use temp folder

   public static File getUserFile (String path) {
      return new File(userProgramDataRoot, path);
   }
   
   /**
    * Print out core information about all people
    * 
    * @see CalendarUtils#print(Calendar,PrintStream)
    */
   public static void print (UserModel model, PrintStream stream) {
      stream.println("USER MODEL FOR "+model.getUserName());
      for (Person person : model.getPeopleManager().getPeople()) {
         stream.print(person);
         Gender gender = person.getGender();
         if ( gender != null ) stream.println(" (" + gender + ")");
         else stream.println();
         MonthDay birthday = person.getBirthday();
         if ( birthday != null ) stream.println("\tBirthday = " + birthday);
         Place location = person.getLocation();
         if ( location != null ) stream.println("\tLocation = " + location);
         String phone = person.getPhoneNumber();
         if ( phone != null ) stream.println("\tPhoneNumber = " + phone);
         for (Relationship relationship : Relationship.values()) {
            Person[] related = person.getRelated(relationship);
            if ( related != null ) {
               stream.print("\t" + relationship + " = ");
               boolean first = true;
               for (Person r : related) {
                  if ( !first ) stream.print(", ");
                  stream.print(r);
                  first = false;
               }
               stream.println();
            }
         }
      }
      stream.println("CALENDAR");
      CalendarUtils.print(model.getCalendar(), stream);
   }

   private UserUtils () {}
}
