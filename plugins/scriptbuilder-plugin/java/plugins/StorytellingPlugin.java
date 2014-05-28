package plugins;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;

public class StorytellingPlugin extends Plugin {

   public StorytellingPlugin (UserModel userModel, CollaborationManager cm) {
      super("Storytelling", userModel, cm);
      addActivity("TellStory", 0, 0, 0, 0, StorytellingSchema.class);
   }
   
   public static final String PERFORMED = "StorytellingPerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   /**
    * For testing plugin by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, StorytellingPlugin.class, "TellStory");
   }

}