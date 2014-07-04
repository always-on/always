package edu.wpi.always;

import com.google.common.collect.ObjectArrays;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.*;
import edu.wpi.disco.rt.util.Utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
     
   // format that Excel will interpret (must be initialized first)
   public final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   public static Logger THIS;
   
   private final PrintWriter writer;

   Logger (boolean enabled) {
      THIS = this;
      if ( enabled )
         try { 
            File file = new File(UserUtils.USER_DIR+"/User."+UserUtils.formatDate()+".csv");
            writer = new PrintWriter(new FileWriter(file, true), true); 
            Utils.lnprint(System.out, "Writing log to: "+file);
         } catch (IOException e) { throw new RuntimeException(e); }
      else writer = null;
   }

   // see always/docs/log-format.txt
   
   private enum Type { ID, ENGAGEMENT, ACTIVITY }
   
   public enum Condition { ALWAYS, LOGIN, REETI }
   
   public static void logId (UserModel model) {
      THIS.log(Type.ID, 
            Always.getAgentType() == AgentType.REETI ? Logger.Condition.REETI :
               Always.isLogin() ? Logger.Condition.LOGIN : Logger.Condition.ALWAYS,
            System.getenv("COMPUTERNAME"), 
            model.getUserName(),
            model.getUserName().isEmpty() ? Always.DATE : new Date(model.getStartTime()),
            Always.DATE, Always.RELEASE);
   }
   
   public static long TOTAL_ENGAGED_TIME;
   
   private static long startEngaged, startRecovering;
   
   public static void logEngagement (EngagementState oldState, EngagementState newState) {
      long engaged = 0;
      long current = System.currentTimeMillis();
      switch (newState) {
         case ENGAGED:
            startEngaged = current;            
            break;
         case RECOVERING:
            engaged = (current - startEngaged) - timeout(); 
              
            startEngaged = 0;   
            startRecovering = current;
            break;
         case IDLE:
            if ( startEngaged != 0 ) {
               engaged = current - startEngaged;
               startEngaged = 0;
            }
            startRecovering = 0;
            break;
         default:
      }
      if ( oldState == EngagementState.RECOVERING ) {
         disengaged += (current - startRecovering) + timeout();
         startRecovering = 0;
      }
      THIS.log(Type.ENGAGEMENT, oldState, newState, (int) (engaged/1000L));
      TOTAL_ENGAGED_TIME += engaged;
   }
   
   private static long timeout () {
      return Math.max(EngagementPerception.ENGAGED_NOT_NEAR_TIMEOUT, 
                      EngagementPerception.ENGAGED_NO_TOUCH_TIMEOUT);
   }
   
   public enum Activity { SESSION, ABOUT, ANECDOTES, CALENDAR, CHECKERS, ENROLL, EXERCISE, EXPLAIN,
                          GREETINGS, GOODBYE, HEALTH, NUTRITION, RUMMY, SKYPE, STORY, TTT, WEATHER }
    
   public static void logActivity (Activity activity, Object... args) {
      log(ObjectArrays.concat(new Object[] {activity}, args, Object.class));
   }
   
   public enum Event { PROPOSED, ACCEPTED, REJECTED, STOPPED, START, END, INTERRUPTION, 
                       SAY, MENU, MENU_EXT, SELECTED, KEYBOARD, MODEL, WON }
   
   private static Logger.Activity activity;
   private static long start, disengaged;
   
   public static void logEvent (Event event, Object... args) {
      Logger.Activity current = SessionSchema.getCurrentLoggerName();
      if ( current == Activity.SESSION )
         log(ObjectArrays.concat(new Object[] {current, event}, args, Object.class));
      else {
         if ( event == Event.START ) {
            activity = current; 
            start = System.currentTimeMillis();
            disengaged = 0;
         } // sic
         if ( event == Event.END ) {
            if ( activity != current) {
               Utils.lnprint(System.out, "WARNING! Unbalanced logger activity END for: "+current);
               log(ObjectArrays.concat(new Object[] {current, event, 0, 0}, args, Object.class));
            } else {
               long duration = System.currentTimeMillis() - start;
               log(ObjectArrays.concat(
                     new Object[] {current, event, 
                        (int) (duration/1000L),
                        (int) ((duration - disengaged)/1000L)},
                     args, Object.class));
            }
            activity = null;
         } else 
            log(ObjectArrays.concat(new Object[] {current, event}, args, Object.class));
      }
   }

   static void log (Object... args) {
      if ( THIS.writer == null ) return;
      StringBuilder line = new StringBuilder(72);
      line.append('"').append(dateFormat.format(new Date())).append('"'); 
      for (Object arg : args) {
         line.append(",\"");
         String field = arg instanceof Date? dateFormat.format((Date) arg) : 
            arg == null ? "" : arg.toString();
         // replace double quotes for safety
         line.append(field.replace('"','\'')).append('"');
      }
      THIS.writer.println(line);
   }
}
