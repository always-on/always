package edu.wpi.always.story;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.dummy.DummySpeechPerceptor;
import edu.wpi.always.story.schema.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.rt.schema.SchemaConfig;

public class StoryPlugin extends Plugin {
   
   public StoryPlugin (UserModel userModel, CollaborationManager cm) {
      super("Story", userModel, cm);
      addActivity("TellStory", 0, 0, 0, 0,
            StorySchema.class, 
            new SchemaConfig(BackChannelSchema.class, 100, false),
            StoryManager.class);
   }
   
   /**
    * For testing Story by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, StoryPlugin.class, "TellStory");
   }

}
