package edu.wpi.always;

import com.google.common.collect.ObjectArrays;
import edu.wpi.always.user.UserUtils;
import edu.wpi.disco.rt.util.Utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

   private static final Logger THIS = new Logger();
   
   private final PrintWriter writer;

   public Logger () {
      try { 
         String file = UserUtils.USER_DIR+UserUtils.formatDate()+".csv";
         writer = new PrintWriter(new FileWriter(file, true), true); 
         Utils.lnprint(System.out, "Writing log to: "+file);
         log("TEST1", "TEST2");/////////////
      } catch (IOException e) { throw new RuntimeException(e); }
   }

   // see always/docs/log-format.txt
   
   private enum Type { SESSION, ACTIVITY }
   
   public enum Session { ATTEMPTED, START, END, CLOSENESS, REMINDER, AGENT, MENU }

   public void logSession (Session session, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {Type.SESSION, session}, args, Object.class));
   }
   
   public enum State { ATTENTION, INITIATING }
   
   public enum Disengagement { GOODBYE, TIMEOUT }
   
   public enum Level { STRANGER, ACQUAINTANCE, COMPANION }
   
   public enum Plugin { ABOUT, ANECDOTES, CALENDAR, CHECKERS, ENROLL, EXERCISE, EXPLAIN,
                        GREETINGS, HEALTH, NUTRITION, SRUMMY, SKYPE, STORY, TTT, WEATHER }
   
   public static void logActivity (Plugin plugin, Object... args) {
      THIS.log(ObjectArrays.concat(new Object[] {Type.ACTIVITY, plugin}, args, Object.class));
   }
   
   // format that Excel will interpret
   private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   private void log (Object... args) {
      StringBuilder line = new StringBuilder(72);
      line.append(dateFormat.format(new Date())); 
      for (Object arg : args) {
         line.append(",\"");
         String field = arg.toString();
         if ( field.indexOf('"') >= 0 ) {
            Utils.lnprint(System.out, "WARNING! Replacing double with single quote in log field: "+field);
            field = field.replace('"','\'');
         }
         line.append(field);
         line.append('"');
      }
      writer.println(line);
   }
}
