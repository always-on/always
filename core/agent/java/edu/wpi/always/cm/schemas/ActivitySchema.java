package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

// FIXME This class will contain methods for coordinating with the session manager,
// such as for interruption and subgoaling

public abstract class ActivitySchema extends SchemaBase {
   
   protected ActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
   }

}
