package edu.wpi.always.calendar;

import edu.wpi.always.*;
import edu.wpi.always.calendar.schema.CalendarSchema;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class CalendarPlugin extends Plugin {
   
   public CalendarPlugin (UserModel userModel, CollaborationManager cm) {
      super("Calendar", userModel, cm);
      addActivity("UseCalendar", 0, 0, 0, 0, CalendarSchema.class, CalendarClient.class); 
   }
   
   /**
    * For testing Calendar by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, CalendarPlugin.class, "UseCalendar");
   }

   @Override
   public void show () {
      // depends on previous state
      container.getComponent(CalendarClient.class).show();
   }
}
