package edu.wpi.always.about;

import edu.wpi.always.*;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.schemas.DiscoAdjacencyPairSchema;
import edu.wpi.always.user.UserModel;
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
      aboutInteraction.load("edu/wpi/always/about/resources/About.xml");
      Plugin.main(args, AboutPlugin.class, "TalkAbout");
   }
   
   public static final String PERFORMED = "AboutPerformed";

   public static String[] getProperties () { return new String[] {PERFORMED}; }

   public static boolean isPerformed () {
      return Always.THIS.getUserModel().isProperty(PERFORMED);
   }
   
   // preload task model
   final static DiscoRT.Interaction aboutInteraction = new DiscoAdjacencyPairSchema.Interaction();
   
   static { if ( Always.ALL_PLUGINS) aboutInteraction.load("edu/wpi/always/about/resources/About.xml"); }
}
