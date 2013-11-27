package edu.wpi.always.explain;

import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class ExplainSchema extends DiscoActivitySchema {

   public ExplainSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
      setSelfStop(true);
      interaction.load("edu/wpi/always/explain/resources/Explain.xml"); 
      start("_ExplainSelf");
   }
}
