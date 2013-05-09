package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.perceptors.MenuPerceptor;
import edu.wpi.cetask.Plan;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;

public class DiscoActivitySchema extends DiscoAdjacencyPairSchema {

   @SuppressWarnings("resource") // for ConsoleWindow
   public DiscoActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            new Interaction(new Agent("agent"), new User("user")));
      new DiscoRT.ConsoleWindow(interaction, getClass().getSimpleName());
   }
   
   protected void start (String id) {
      Plan plan = interaction.addTop("WeatherStranger");
      plan.getGoal().setShould(true);
      interaction.push(plan);
   }

}
