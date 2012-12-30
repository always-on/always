package edu.wpi.always.cm.schemas;

// not currently used because no implementation of perceptor

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class MovementTrackerSchema extends SchemaBase {

   private final MovementPerceptor movementPerceptor;

   public MovementTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, MovementPerceptor movementPerceptor) {
      super(behaviorReceiver, resourceMonitor);
      this.movementPerceptor = movementPerceptor;
   }

   @Override
   public void run () {
      MovementPerception perception = movementPerceptor.getLatest();
      if ( perception != null && perception.getPoint() != null ) {
         BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0)
               .dueIn(100).timeRemaining(0.01).build();
         propose(new GazeBehavior(perception.getPoint()), m);
      } else {
         proposeNothing();
      }
   }
}
