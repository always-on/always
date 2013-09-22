package edu.wpi.always.srummy;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class SrummyPlugin extends Plugin {

   public SrummyPlugin (UserModel userModel, CollaborationManager cm) {
      super("Rummy", userModel, cm);
      addActivity("PlayRummy", 0, 0, 0, 0, SrummySchema.class, SrummyClient.class); 
   }

   /**
    * For testing TicTacToe by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, SrummyPlugin.class, "PlayRummy");
   }

}
