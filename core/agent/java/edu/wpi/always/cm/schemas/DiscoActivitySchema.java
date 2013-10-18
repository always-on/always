package edu.wpi.always.cm.schemas;

import edu.wpi.always.Always;
import edu.wpi.cetask.Plan;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

/**
 * Note: This schema will automatically stop (cancel) when
 * there is nothing left for the user or agent to say.
 * It can also be ended abrubtly by calling '$schema.cancel()'
 * in the 'eval' attribute of a D4g element.
 */
public class DiscoActivitySchema extends DiscoAdjacencyPairSchema {

   private final DiscoRT.ConsoleWindow console;
   
   public DiscoActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
      console = new DiscoRT.ConsoleWindow(interaction, getClass().getSimpleName());
   }
   
   protected void start (String id) {
      Plan plan = interaction.addTop(id);
      plan.getGoal().setShould(true);
      interaction.push(plan);
   }
   
   @Override
   public void cancel () {
      super.cancel();
      console.close();
   }

}
