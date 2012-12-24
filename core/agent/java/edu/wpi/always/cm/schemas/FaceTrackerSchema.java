package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;

public class FaceTrackerSchema extends SchemaImplBase {

   private final FacePerceptor facePerceptor;

   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitory, FacePerceptor facePerceptor) {
      super(behaviorReceiver, resourceMonitory);
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
