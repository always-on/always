package edu.wpi.always.about;

import edu.wpi.always.Plugin;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.DiscoRT;

public class AboutPlugin extends Plugin {
   
   public AboutPlugin (UserModel userModel, CollaborationManager cm) {
      super("About", userModel, cm);
      addActivity("TalkAbout", 0, 0, 0, 0, AboutSchema.class); 
   }

   /**
    * For testing About by itself
    */
   public static void main (String[] args) {
      Plugin.main(args, AboutPlugin.class, "TalkAbout");
   }
   
   // preload task model
   final static DiscoRT.Interaction aboutInteraction = 
         new DiscoRT.Interaction(new Agent("agent"), new User("user"));
   static { aboutInteraction.load("edu/wpi/always/about/resources/About.xml"); }
}
