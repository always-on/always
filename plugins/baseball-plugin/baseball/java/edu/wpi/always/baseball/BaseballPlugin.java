package edu.wpi.always.baseball;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

// TODO Make this work with live daily data

public class BaseballPlugin extends Plugin {
   
   public BaseballPlugin (UserModel userModel, CollaborationManager cm) {
      super("Baseball", userModel, cm);
      addActivity("DiscussBaseball", 0, 0, 0, 0, BaseballSchema.class); 
   }
 
   /**
    * For testing Baseball by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, BaseballPlugin.class, "DiscussBaseball");
   }
  
}
