package plugins;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class AnecdotesPlugin extends Plugin {

   public AnecdotesPlugin (UserModel userModel, CollaborationManager cm) {
      super("Anecdotes", userModel, cm);
      addActivity("HearAnecdotes", 0, 0, 0, 0, AnecdotesSchema.class);
   }
 
   public static final String PERFORMED = "AnecdotesPerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, AnecdotesPlugin.class, "HearAnecdotes");
   }

}