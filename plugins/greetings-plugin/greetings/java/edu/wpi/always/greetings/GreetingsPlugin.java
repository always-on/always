package edu.wpi.always.greetings;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoRT;

public class GreetingsPlugin extends Plugin {
   
   public GreetingsPlugin (UserModel userModel, CollaborationManager cm) {
      super("Greetings", userModel, cm);
      addActivity("GreetUser", 0, 0, 0, 0, GreetingsSchema.class); 
      addActivity("SayGoodbye", 0, 0, 0, 0, GoodbyeSchema.class); 
   }

   /**
    * For testing Greetings by itself
    */
   public static void main (String[] args) {
      greetingsInteraction.load("edu/wpi/always/greetings/resources/Greetings.xml"); 
      Plugin.main(args, GreetingsPlugin.class, "GreetUser");
   }
  
   // preload task model
   final static DiscoRT.Interaction greetingsInteraction = 
         new DiscoRT.Interaction(new Agent("agent"), new User("user"));
   static { if ( Always.ALL_PLUGINS) greetingsInteraction.load("edu/wpi/always/greetings/resources/Greetings.xml"); }

   // plugin-specific properties
   
   public static final String POOR_SLEEP_REPORTS = "GreetingsPoorSleepReports";
   
   public static String[] getProperties () {
      return new String[] { POOR_SLEEP_REPORTS };         
   }
}
