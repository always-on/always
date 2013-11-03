package plugins;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class AnecdotesPlugin extends Plugin {

   public AnecdotesPlugin (UserModel userModel, CollaborationManager cm) {
      super("Anecdotes", userModel, cm);
      addActivity("TellAnecdotes", 0, 0, 0, 0, AnecdotesSchema.class);
   }

   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, AnecdotesPlugin.class, "TellAnecdotes");
   }

}