package edu.wpi.always.explain;

import edu.wpi.always.*;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class ExplainSchema extends DiscoActivitySchema {

   private static boolean running;

   @Override
   public void dispose () { 
      super.dispose();
      running = false; 
   } 
   
   public ExplainSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always, 
            ExplainPlugin.explainInteraction);
      if ( running ) throw new IllegalStateException("ExplainSchema already running!");
      running = true;
      always.getUserModel().setProperty(ExplainPlugin.PERFORMED, true);
      setSelfStop(true);
      interaction.clear();
      start("_ExplainSelf");
   }
   
   public static void log (Logger.Activity activity) {
      Logger.logActivity(Logger.Activity.EXPLAIN, activity);
   }
}
