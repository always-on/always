package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public abstract class ActivitySchema extends SchemaBase {

   protected ActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
      setNeedsFocusResource(true);
   }

   
}
