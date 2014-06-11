package edu.wpi.always.greetings;

import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.*;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class GreetingsSchema extends DiscoActivitySchema {

   private static boolean running;

   @Override
   public void dispose () { 
      super.dispose();
      running = false; 
   } 
   
   private final UserModel model;
   
   public GreetingsSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, 
            GreetingsPlugin.greetingsInteraction);
      model = always.getUserModel();
      if ( behaviorReceiver == null ) return; // for always_disco testing
      if ( running ) throw new IllegalStateException("GreetingsSchema already running!");
      running = true;
      setSelfStop(true);
      interaction.clear();
      switch (UserUtils.getTimeOfDay()) {
         case MORNING:
            switch (Always.THIS.getUserModel().getCloseness()) {
               case STRANGER: 
               case ACQUAINTANCE: 
                  start("_MorningGreetings");
                  break;
               case COMPANION: 
                  start("_MorningGreetingsCompanion"); 
                  break;
            }
            break;
         case AFTERNOON:
            start("_AfternoonGreetings" );
            break;
         case EVENING:
            start("_EveningGreetings" );
            break;
         case NIGHT:
            start("_NightGreetings"); 
            break;
      }
   }
   
   // to support calling properly from JavaScript
   public void setPoorSleepReports (double n) {
      model.setProperty(GreetingsPlugin.POOR_SLEEP_REPORTS, (int) n);
   }
}
