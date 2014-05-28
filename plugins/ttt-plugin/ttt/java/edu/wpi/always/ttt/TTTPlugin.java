package edu.wpi.always.ttt;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class TTTPlugin extends Plugin {

   public TTTPlugin (UserModel userModel, CollaborationManager cm) {
      super("TicTacToe", userModel, cm);
      addActivity("PlayTicTacToe", 0, 0, 0, 0, TTTSchema.class, TTTClient.class); 
   }
   
   public static final String PERFORMED = "TicTacToePerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing TicTacToe by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, TTTPlugin.class, "PlayTicTacToe");
   }

   @Override
   public void show () {
      container.getComponent(TTTClient.class).show();
   }
}
