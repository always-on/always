package edu.wpi.always.cm.schemas;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

/**
 * Base schema for activities.  Each activity should have exactly one such schema. 
 */
public abstract class ActivitySchema extends SchemaBase {

   public static final double SPECIFICITY = 0.7;
   
   protected ActivitySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
      setNeedsFocusResource(true);
   }

   
}
