package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.Scheduler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import edu.wpi.always.*;

public class FaceTrackerSchema extends SchemaBase {

   private final FacePerceptor facePerceptor;

   private Always.AgentType AgentOrReetiOrBoth()
   {
      return Always.getAgentType();
   }
   
   public FaceTrackerSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, FacePerceptor facePerceptor) {
      super(behaviorReceiver, behaviorHistory);
      this.facePerceptor = facePerceptor;
   }

   @Override
   public void run () {
      FacePerception perception = null;
      if(AgentOrReetiOrBoth() == Always.AgentType.Unity || AgentOrReetiOrBoth() == Always.AgentType.Both)
      {
         perception = facePerceptor.getLatest();
      }
      if(AgentOrReetiOrBoth() == Always.AgentType.Reeti || AgentOrReetiOrBoth() == Always.AgentType.Both)
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
