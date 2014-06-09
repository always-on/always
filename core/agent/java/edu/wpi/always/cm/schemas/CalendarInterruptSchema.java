package edu.wpi.always.cm.schemas;

import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.calendar.CalendarEntry;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.util.*;
import org.joda.time.DateTime;

public class CalendarInterruptSchema extends SchemaBase {
   
   private final Calendar calendar;
   private final List<CalendarEntry> done = new ArrayList<CalendarEntry>();
   
   public CalendarInterruptSchema (Calendar calendar) {
      super(null, null); 
      this.calendar = calendar;
   }

   // for testing
   public static boolean interrupt () {
      return SessionSchema.interrupt("_CalendarInterruption");
   }
   
   @Override
   public void run () {
      DateTime now = new DateTime();
      for (CalendarEntry entry : calendar.retrieve(now, now.plusMinutes(20))) {
         if ( !done.contains(entry) ) {
            if ( interrupt() ) done.add(entry);
         }
      }
   }
   
}
  