package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class FaceTrackerSchema extends SchemaBase {
   
   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      super(behaviorReceiver, behaviorHistory);
   }

   // reducing memory use for long-running
   private final static FaceTrackBehavior BEHAVIOR = new FaceTrackBehavior();
   private final static BehaviorMetadata META = new BehaviorMetadataBuilder().specificity(0.1).build(); 

   @Override
   public void run () {
      // always propose behavior (realizer will take care of face dropouts)
      propose(BEHAVIOR, META);
   }
}
