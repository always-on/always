package edu.wpi.always.rummy;

import edu.wpi.always.*;
import edu.wpi.always.cm.ICollaborationManager;
import edu.wpi.always.user.UserModel;

public class RummyPlugin extends Plugin {
   
   public RummyPlugin (UserModel userModel, ICollaborationManager cm) {
      super("Rummy", userModel, cm);
      addActivity("PlayRummy", 0, 0, 0, 0, RummySchema.class, RummyClient.class); 
   }
   
   /**
    * For testing Rummy by itself
    */
   public static void main (String[] args) {
      new Always(true, RummyPlugin.class, "PlayRummy").start();
   }
  

  
}
