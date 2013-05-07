package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.Scheduler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class FaceTrackerSchema extends SchemaBase {

   private final FacePerceptor facePerceptor;

   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, FacePerceptor facePerceptor) {
      super(behaviorReceiver, behaviorHistory);
      this.facePerceptor = facePerceptor;
   }

   @Override
   public void run () {
      FacePerception perception = facePerceptor.getLatest();
      if ( perception != null ) {
         BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.05)
               .build();
         propose(new FaceTrackBehavior(), m);
      } else {
         proposeNothing();
      }
   }
}
