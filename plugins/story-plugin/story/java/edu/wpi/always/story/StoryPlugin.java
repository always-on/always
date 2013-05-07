package edu.wpi.always.story;

import edu.wpi.always.*;
import edu.wpi.always.cm.ICollaborationManager;
import edu.wpi.always.cm.perceptors.dummy.DummySpeechPerceptor;
import edu.wpi.always.story.schema.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.ComponentRegistry;
import org.picocontainer.*;

public class StoryPlugin extends Plugin {
   
   public StoryPlugin (UserModel userModel, ICollaborationManager cm) {
      super("Story", userModel, cm);
      addActivity("RecordStory", 0, 0, 0, 0,
            new SchemaConfig(StorySchema.class, 1000, true),
            new SchemaConfig(BackChannelSchema.class, 100, false),
            StoryManager.class,
            DummySpeechPerceptor.class);
   }
   
   /**
    * For testing Story by itself
    */
   public static void main (String[] args) {
      new Always(true, StoryPlugin.class, "RecordStory").start();
   }
  

  
}
