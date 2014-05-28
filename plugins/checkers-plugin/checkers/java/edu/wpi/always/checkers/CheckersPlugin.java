package edu.wpi.always.checkers;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class CheckersPlugin extends Plugin {

   public CheckersPlugin (UserModel userModel, CollaborationManager cm) {
      super("Checkers", userModel, cm);
      addActivity("PlayCheckers", 0, 0, 0, 0, CheckersSchema.class, CheckersClient.class); 
   }
   
   public static final String PERFORMED = "CheckersPerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
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
