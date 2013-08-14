package edu.wpi.always.cm.schemas;

// not currently used because no implementation of perceptor

import edu.wpi.always.cm.perceptors.MovementPerception;
import edu.wpi.always.cm.perceptors.MovementPerceptor;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import edu.wpi.disco.rt.behavior.BehaviorMetadataBuilder;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
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
