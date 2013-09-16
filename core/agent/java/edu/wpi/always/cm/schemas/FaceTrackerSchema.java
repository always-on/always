package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.Scheduler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import edu.wpi.always.*;

public class FaceTrackerSchema extends SchemaBase {

   private final FacePerceptor facePerceptor;
   
   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, FacePerceptor facePerceptor) {
      super(behaviorReceiver, behaviorHistory);
      this.facePerceptor = facePerceptor;
   }

   @Override
   public void run () {
      FacePerception perception = null;
      Always.AgentType agentType = Always.getAgentType();
      if(agentType == Always.AgentType.Both)
      {
         perception = facePerceptor.getLatest();
         perception = facePerceptor.getReetiLatest();
      }
      else if(agentType == Always.AgentType.Unity )
      {
         perception = facePerceptor.getLatest();
      }
      else if(agentType == Always.AgentType.Reeti)
      {
         perception = facePerceptor.getReetiLatest();
      }
      if ( perception != null ) {
         BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.05)
               .build();
         propose(new FaceTrackBehavior(), m);
      } else {
         proposeNothing();
      } 
   }
}
