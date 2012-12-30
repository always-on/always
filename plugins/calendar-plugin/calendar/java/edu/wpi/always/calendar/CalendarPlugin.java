package edu.wpi.always.calendar;

import edu.wpi.always.*;
import edu.wpi.always.calendar.schema.CalendarSchema;

public class CalendarPlugin extends PluginBase {
   
   public CalendarPlugin () { 
      super("UseCalendar", CalendarSchema.class, CalendarClient.class); 
   }
   
   /**
    * For testing Calendar by itself
    */
   public static void main (String[] args) {
      new Always(true, CalendarPlugin.class, null).start();
   }
  

  
}
