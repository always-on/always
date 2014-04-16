package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class FaceTrackerSchema extends SchemaBase {
   
   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
   }

   @Override
   public void run () {
      // always propose behavior (realizer will take care of dropouts)
      propose(new FaceTrackBehavior(),
            new BehaviorMetadataBuilder().specificity(0.1).build());
   }
}
