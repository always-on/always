package edu.wpi.always.cm.schemas;

import edu.wpi.always.Always;
import edu.wpi.cetask.Plan;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class DiscoActivitySchema extends DiscoAdjacencyPairSchema {

   private final DiscoRT.ConsoleWindow console;
   
   public DiscoActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            new Interaction(new Agent("agent"), new User("user")));
      always.init(interaction);
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
