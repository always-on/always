package edu.wpi.always.greetings;

import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class GreetingsSchema extends DiscoActivitySchema {

   public GreetingsSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
      setSelfStop(true);
      interaction.load("edu/wpi/always/greetings/resources/Greetings.xml");
      start("_Greetings");
   }
}
