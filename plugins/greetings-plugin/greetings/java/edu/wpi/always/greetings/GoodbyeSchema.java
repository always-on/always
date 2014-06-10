package edu.wpi.always.greetings;

import edu.wpi.always.*;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.UserUtils;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class GoodbyeSchema extends DiscoActivitySchema {
   
   public GoodbyeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, 
            GreetingsPlugin.greetingsInteraction);
      if ( behaviorReceiver == null ) return; // for always_disco testing
      interaction.clear();
      switch (UserUtils.getTimeOfDay()) {
         case MORNING:
         case AFTERNOON:
         case EVENING:
            start("_Goodbye");
            break;
         case NIGHT:
            start("_GoodNight"); 
            break;
      }
   }
   
   public static void log (UserUtils.TimeOfDay tod) {
      Logger.logActivity(Logger.Activity.GREETINGS, tod);
   }
}
