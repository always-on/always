package edu.wpi.always.enroll;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.enroll.schema.EnrollSchema;
import edu.wpi.always.user.UserModel;

public class EnrollPlugin extends Plugin{

   public EnrollPlugin(UserModel userModel, CollaborationManager cm) {
      super("Enroll", userModel, cm);
      addActivity("EnrollUser", 0, 0, 0, 0, EnrollSchema.class, EnrollClient.class); 
   }

   /**
    * For testing Enroll by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, EnrollPlugin.class, "EnrollUser");
      //always.processArgs(args);
      //always.start();
   }
}
