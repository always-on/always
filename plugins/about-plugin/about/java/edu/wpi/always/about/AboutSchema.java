package edu.wpi.always.about;


import edu.wpi.always.*;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class AboutSchema extends DiscoActivitySchema {
    
   private static boolean running;

   @Override
   public void dispose () { 
      super.dispose();
      running = false; 
   } 
   
   public AboutSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always,
            AboutPlugin.aboutInteraction);
      if ( running ) throw new IllegalStateException("GreetingsSchema already running!");
      running = true;
      always.getUserModel().setProperty(AboutPlugin.PERFORMED, true);
      interaction.clear();
      start("_TalkAbout");
              
   }

   enum Positive { POSITIVE, NOT_POSITIVE }
   enum Negative { NEGATIVE, NOT_NEGATIVE }
   enum Exit { EARLY, NORMAL }
   
   public static void log (Positive positive, Negative negative, Exit exit) {
      Logger.logActivity(Logger.Activity.ABOUT, positive, negative, exit);
   }
   
}
