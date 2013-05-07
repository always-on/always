package edu.wpi.disco.rt.schema;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;

/**
 * Schema that requires focus
 */
public abstract class FocusSchema extends SchemaBase {
 
   protected FocusSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
      setNeedsFocusResource(true);
   }
 
}
