package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.GazeBehavior;

public class MovementTrackerSchema extends SchemaImplBase {

   private final MovementPerceptor movementPerceptor;

   public MovementTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, MovementPerceptor movementPerceptor) {
      super(behaviorReceiver, resourceMonitor);
      this.movementPerceptor = movementPerceptor;
   }

   @Override
   public void run () {
      MovementPerception perception = movementPerceptor.getLatest();
      if ( perception != null && perception.movementLocation() != null ) {
         BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0)
               .dueIn(100).timeRemaining(0.01).build();
         propose(new GazeBehavior(perception.movementLocation()), m);
      } else {
         proposeNothing();
      }
   }
}
