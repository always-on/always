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
   
   public final static Logger.Activity LOGGER_NAME = Logger.Activity.ABOUT;
   
   public enum Positive { POSITIVE, NOT_POSITIVE }
   public enum Negative { NEGATIVE, NOT_NEGATIVE }
   public enum Exit { EARLY, NORMAL }
   
   public static void log (Positive positive, Negative negative, Exit exit) {
      Logger.logActivity(LOGGER_NAME, positive, negative, exit);
   }
   
   public AboutSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always,
            AboutPlugin.aboutInteraction, LOGGER_NAME);
      if ( running ) throw new IllegalStateException("GreetingsSchema already running!");
      running = true;
      always.getUserModel().setProperty(AboutPlugin.PERFORMED, true);
      interaction.clear();
      start("_TalkAbout");
              
   }
   
}
