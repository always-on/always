package edu.wpi.always;

import com.google.common.collect.ObjectArrays;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.user.UserUtils;
import edu.wpi.disco.rt.util.Utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
     
   // format that Excel will interpret (must be initialized first)
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
   
   private enum Type { ID, ENGAGEMENT, SESSION, ACTIVITY }
   
   public enum Condition { ALWAYS, LOGIN, REETI }
   
   public static void logId (Condition condition, String machine, Date installed, Date booted) {
      THIS.log(Type.ID, machine, condition, installed, booted);
   }
   
   public static void logEngagement (EngagementState oldState, EngagementState newState) {
      THIS.log(Type.ENGAGEMENT, oldState, newState);
   }
   
   public enum Session { START, END, INTERRUPTION, AGENT, MENU }

   public static void logSession (Session session, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {Type.SESSION, session}, args, Object.class));
   }
   
   public enum Activity { ABOUT, ANECDOTES, CALENDAR, CHECKERS, ENROLL, EXERCISE, EXPLAIN,
                          GREETINGS, HEALTH, NUTRITION, SRUMMY, SKYPE, STORY, TTT, WEATHER }
   
   public static void logActivity (Activity activity, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {Type.ACTIVITY, activity}, args, Object.class));
   }
    
   private void log (Object... args) {
      StringBuilder line = new StringBuilder(72);
      line.append('"').append(dateFormat.format(new Date())).append('"'); 
      for (Object arg : args) {
         line.append(",\"");
         String field = arg instanceof Date? dateFormat.format((Date) arg) : arg.toString();
         if ( field.indexOf('"') >= 0 ) {
            Utils.lnprint(System.out, "WARNING! Replacing double with single quote in log field: "+field);
            field = field.replace('"','\'');
         }
         line.append(field).append('"');
      }
      writer.println(line);
   }
}
