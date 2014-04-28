package plugins;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class StorytellingPlugin extends Plugin {

   public StorytellingPlugin (UserModel userModel, CollaborationManager cm) {
      super("Storytelling", userModel, cm);
      addActivity("TellStory", 0, 0, 0, 0, StorytellingSchema.class);
   }

   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, StorytellingPlugin.class, "TellStory");
   }

}