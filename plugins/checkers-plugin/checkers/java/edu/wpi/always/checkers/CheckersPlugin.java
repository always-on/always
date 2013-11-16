package edu.wpi.always.checkers;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class CheckersPlugin extends Plugin {

   public CheckersPlugin (UserModel userModel, CollaborationManager cm) {
      super("Checkers", userModel, cm);
      addActivity("PlayCheckers", 0, 0, 0, 0, CheckersSchema.class, CheckersClient.class); 
   }

   /**
    * For testing Checkers by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, CheckersPlugin.class, "PlayCheckers");
   }

   public void show () {
      container.getComponent(CheckersClient.class).show();
   }
}
