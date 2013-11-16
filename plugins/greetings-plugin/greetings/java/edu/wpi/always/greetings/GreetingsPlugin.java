package edu.wpi.always.greetings;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class GreetingsPlugin extends Plugin {
   
   public GreetingsPlugin (UserModel userModel, CollaborationManager cm) {
      super("Greetings", userModel, cm);
      addActivity("GreetUser", 0, 0, 0, 0, GreetingsSchema.class); 
   }

   /**
    * For testing Greetings by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, GreetingsPlugin.class, "GreetUser");
   }
  
   // plugin-specific properties
   
   public static final String POOR_SLEEP_REPORTS = "GreetingsPoorSleepReports";
   
   public static String[] getProperties () {
      return new String[] { POOR_SLEEP_REPORTS };         
   }
}
