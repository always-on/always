package edu.wpi.always.user;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import org.joda.time.*;
import org.picocontainer.BindKey;
import edu.wpi.always.*;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.*;
import edu.wpi.always.user.people.Person.Gender;
import edu.wpi.always.user.people.Person.Relationship;
import edu.wpi.always.user.places.Place;
import edu.wpi.cetask.*;

public abstract class UserUtils {

   /**
    * Folder where user model is stored.  
    * See initialization in Always constructor
    */
   public static String USER_DIR;
   
   /**
    * Filename in USER_DIR for user model.
    * 
    * @see Always#main(String[])
    */
   public static String USER_FILE;
   
   /**
    * Optional argument is USER_FILE
    */
   public static void main (String[] args) {
      if ( args != null && args.length > 0 && args[0].length() > 0 )
         USER_FILE = args[0];
      Always always = new Always(true, true);
      // to get plugin classes 
      for (TaskClass task : new TaskEngine().load("Activities.xml").getTaskClasses())
         Plugin.getPlugin(task);
      UserModel model = always.getUserModel();
      if ( model.getUserName() == null )
         System.err.println("Could not load model from "+
             always.getContainer().getComponent(
                   BindKey.bindKey(File.class, UserModel.UserOntologyLocation.class)));
      else print(model, System.out);
   }
   
   /**
    * @returns last modified file in USER_DIR starting with "User." and
    * ending with ".owl", or null if none.
    */
   public static File lastModified () {
      File last = null;
      for (File file : new File(USER_DIR).listFiles()) {
         String name = file.getName();
         if ( name.startsWith("User.") && name.endsWith(".owl") ) {
            if ( last == null ) last = file;
            else if ( file.lastModified() > last.lastModified() )
               last = file;
         }
      }
      return last;
   }
   
   public static String formatDate () {
      return new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(Always.getSessionDate());
   }
   
   /**
    * Print out core information about all people
    * 
    * @see CalendarUtils#print(Calendar,PrintStream)
    */
   public static void print (UserModel model, PrintStream stream) {
      stream.println();
      stream.println("USER MODEL FOR "+model.getUserName());
      stream.println();
      stream.println("Closeness: "+model.getCloseness());
      stream.println("Sessions: "+model.getSessions());
      stream.println("StartTime: "+new DateTime(model.getStartTime()));
      stream.println();
      for (Person person : model.getPeopleManager().getPeople(true)) {
         stream.print(person);
         Gender gender = person.getGender();
         if ( gender != null ) stream.println(" (" + gender + ")");
         else stream.println();
         int age = person.getAge();
         if ( age != 0 ) stream.println("\tAge = " + age);
         MonthDay birthday = person.getBirthday();
         if ( birthday != null ) stream.println("\tBirthday = " + birthday);
         Place location = person.getLocation();
         if ( location != null ) stream.println("\tLocation = " + location);
         String skype = person.getSkypeNumber();
         if ( skype != null ) stream.println("\tSkypeNumber = " + skype);
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
         // plugin specific per person properties
         Person.AboutStatus status = person.getAboutStatus();
         if ( status != null ) stream.println("\tAboutStatus = "+status);
         String comment = person.getAboutComment();
         if ( comment != null ) stream.println("\tAboutComment = "+comment);
         boolean mentioned = person.isAboutMentioned();
         if ( mentioned ) stream.println("\tAboutMentioned = "+mentioned);
      }
      stream.println("\nPLUGIN-SPECIFIC USER PROPERTIES\n");
      for (Class<? extends Plugin> plugin : Plugin.getPlugins()) {
         boolean hasProperties = false;
         try {
            for (String property : (String[]) plugin.getMethod("getProperties").invoke(null)) {
               stream.println("\t"+property+" = "+model.getProperty(property));
               hasProperties = true;
            }
         } catch (Exception e) {}
         if ( hasProperties) stream.println();
      }
      stream.println("CALENDAR\n");
      CalendarUtils.print(model.getCalendar(), stream);
      stream.println();
   }

   private static final String regex = "\\d{3}-\\d{3}-\\d{4}";

   public static boolean isPhoneNumberValid(String phoneNumber) {
      return Pattern.matches(regex, phoneNumber);
   }

   public static boolean isInteger(String s) {
      boolean result = true;
      try { 
         int age = Integer.parseInt(s); 
         if(0 > age || age > 110){
            result = false;
         }
      } catch(NumberFormatException e) { 
         result = false; 
      }
      return result;
   }

   public static boolean isValidDayOfMonth(int month, int day){
      boolean result = true;
      if(1 > day || day > 31){
         result = false;
      }
      else{
         if(month == 2){
            if(day > 29) {
               result = false;
            }
         }
         else if(month == 4 || month == 6 || month == 9 || month == 11) {
            if(day == 31){
               result = false;
            }
         }
         else{
            result = true;
         }
      }
      return result;
   }

   private UserUtils () {}
}
