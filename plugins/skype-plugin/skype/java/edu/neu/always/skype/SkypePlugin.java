package edu.neu.always.skype;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class SkypePlugin extends Plugin {
   
   public SkypePlugin (UserModel userModel, CollaborationManager cm) {
      super("Skype", userModel, cm);
      addActivity("UseSkype", 0, 0, 0, 0, SkypeSchema.class); 
   }

   /**
    * For testing Skype by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, SkypePlugin.class, "UseSkype");
   }
  

  
}
