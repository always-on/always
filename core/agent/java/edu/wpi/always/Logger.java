package edu.wpi.always;

import com.google.common.collect.ObjectArrays;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.always.user.UserUtils;
import edu.wpi.disco.rt.util.Utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
     
   // format that Excel will interpret (must be initialized first)
   public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   // guarantee only one logger
   public static final Logger THIS = new Logger();
   
   private final PrintWriter writer;

   private Logger () {
      try { 
         File file = new File(UserUtils.USER_DIR+"/User."+UserUtils.formatDate()+".csv");
         writer = new PrintWriter(new FileWriter(file, true), true); 
         Utils.lnprint(System.out, "Writing log to: "+file);
      } catch (IOException e) { throw new RuntimeException(e); }
   }

   // see always/docs/log-format.txt
   
   private enum Type { ID, ENGAGEMENT, ACTIVITY }
   
   public enum Condition { ALWAYS, LOGIN, REETI }
   
   public static void logId (Condition condition, String machine, String userName, Date installed, Date booted, String release) {
      THIS.log(Type.ID, condition, machine, userName, installed, booted, release);
   }
   
   public static void logEngagement (EngagementState oldState, EngagementState newState) {
      THIS.log(Type.ENGAGEMENT, oldState, newState);
   }
   
   public enum Activity { SESSION, ABOUT, ANECDOTES, CALENDAR, CHECKERS, ENROLL, EXERCISE, EXPLAIN,
                          GREETINGS, GOODBYE, HEALTH, NUTRITION, RUMMY, SKYPE, STORY, TTT, WEATHER }
    
   public static void logActivity (Activity activity, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {activity}, args, Object.class));
   }
   
   public enum Event { PROPOSED, ACCEPTED, REJECTED, STOPPED, START, END, INTERRUPTION, 
                       SAY, MENU, EXTENSION, SELECTED, KEYBOARD, MODEL, WON }
   
   public static void logEvent (Event event, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {SessionSchema.getCurrentLoggerName(), event}, args, Object.class));
   }

   private void log (Object... args) {
      StringBuilder line = new StringBuilder(72);
      line.append('"').append(dateFormat.format(new Date())).append('"'); 
      for (Object arg : args) {
         line.append(",\"");
         String field = arg instanceof Date? dateFormat.format((Date) arg) : 
            arg == null ? "" : arg.toString();
         // replace double quotes for safety
         line.append(field.replace('"','\'')).append('"');
      }
      writer.println(line);
   }
}
