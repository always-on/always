package edu.wpi.always.explain;

import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class ExplainSchema extends DiscoActivitySchema {

   public ExplainSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      interaction.load("edu/wpi/always/explain/resources/Explain.xml"); 
      start("_ExplainSelf");
   }
}
