package edu.wpi.always.calendar;

import org.picocontainer.MutablePicoContainer;
import edu.wpi.always.*;
import edu.wpi.always.calendar.schema.CalendarSchema;
import edu.wpi.always.user.UserModel;

public class CalendarPlugin extends Plugin {
   
   public CalendarPlugin (UserModel userModel) {
      super("Calendar", userModel);
      addActivity("UseCalendar", 0, 0, 0, 0, CalendarSchema.class, CalendarClient.class); 
   }
   
   /**
    * For testing Calendar by itself
    */
   public static void main (String[] args) {
      new Always(true, CalendarPlugin.class, "UseCalendar").start();
   }
  

  
}
