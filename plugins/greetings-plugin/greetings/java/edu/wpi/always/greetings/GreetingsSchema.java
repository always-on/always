package edu.wpi.always.greetings;

import org.joda.time.LocalTime;
import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.always.user.UserModel;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class GreetingsSchema extends DiscoActivitySchema {

   public static int HOUR = -1;  // for testing
   
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
      if ( running ) throw new IllegalStateException("GreetingsSchema already running!");
      running = true;
      setSelfStop(true);
      interaction.clear();
      model = always.getUserModel();
      if ( HOUR < 0 ) HOUR = LocalTime.now().getHourOfDay();
      if ( HOUR > 4 && HOUR < 12 )
         switch (Always.THIS.getUserModel().getCloseness()) {
            case Stranger: 
            case Acquaintance: 
               start("_MorningGreetings");
               break;
            case Companion: 
               start("_MorningGreetingsCompanion"); 
               break;
         }
      else
          start( (HOUR <= 4 || HOUR > 22) ? "_NightGreetings" : 
                 HOUR > 18 ? "_EveningGreetings" :
                 "_AfternoonGreetings" );
   }
   
   // to support calling properly from JavaScript
   public void setPoorSleepReports (double n) {
      model.setProperty(GreetingsPlugin.POOR_SLEEP_REPORTS, (int) n);
   }
}
