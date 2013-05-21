package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.schema.*;
import java.util.Arrays;

public class EngagementSchema extends SchemaBase {

   private final MenuTurnStateMachine stateMachine;
   private final EngagementPerceptor engagementPerceptor;
   private final FacePerceptor facePerceptor;
   private SchemaManager schemaManager;

   public EngagementSchema (BehaviorProposalReceiver behaviorReceiver,
         EngagementPerceptor engagementPerceptor,
         FacePerceptor facePerceptor, BehaviorHistory behaviorHistory,
         ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor,
         SchemaManager schemaManager) {
      super(behaviorReceiver, behaviorHistory);
      this.engagementPerceptor = engagementPerceptor;
      this.facePerceptor = facePerceptor;
      this.schemaManager = schemaManager;
      stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
            menuPerceptor, new RepeatMenuTimeoutHandler());
      stateMachine.setSpecificityMetadata(.9);
   }

   private EngagementState lastState = null;

   @Override
   public void run () {
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.05)
            .build();
      EngagementPerception engPerception = engagementPerceptor
            .getLatest();
      FacePerception facePerception = facePerceptor.getLatest();
      if ( engPerception != null ) {
         switch (engPerception.getState()) {
         case Idle:
            propose(Behavior.newInstance(new IdleBehavior(false)), m);
            break;
         case Attention:
            if ( facePerception != null && facePerception.getPoint() != null )
               propose(new FaceTrackBehavior(), m);
            break;
         case Initiation:
            propose(Behavior.newInstance(new FaceTrackBehavior(),
                  new SpeechBehavior("Hi")), m);
            break;
         case Engaged:
            // FIXME Disabled engagement dialogue for testing...
            //if ( lastState != EngagementState.Engaged )
            //   stateMachine.setAdjacencyPair(new EngagementDialog(schemaManager));
            //propose(stateMachine);
            break;
         case Recovering:
            propose(Behavior.newInstance(new FaceTrackBehavior(),
                  new SpeechBehavior("Are you still there"), new MenuBehavior(
                        Arrays.asList("Yes"))), m);
            break;
         }
         lastState = engPerception.getState();
      } else
         lastState = null;
   }
}
