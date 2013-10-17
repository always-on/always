package edu.neu.always.skype;

import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

// this is the schema for initiating a Skype call

public class SkypeSchema extends ActivityStateMachineSchema {

   private final ShoreFacePerceptor shore;
   
   public SkypeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ShoreFacePerceptor shore) {
      super(new Test(shore),  // TODO: need to provide real initial AdjacencyPair here 
            behaviorReceiver, behaviorHistory, resourceMonitor,
				menuPerceptor);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
   }

   @Override
   public void dispose () { 
      if ( shore != null ) shore.start(); 
   }
 
   // to test camera release and restart
   
   public static class Test extends MultithreadAdjacencyPair<Void> {

      private final ShoreFacePerceptor shore;
      
      public Test (ShoreFacePerceptor shore) {
         super("This is a test", null);
         this.shore = shore;
         choice("Ok", new DialogStateTransition() {
            public AdjacencyPair run () { throw new RuntimeException(); }
         });
      }
      
      @Override
      public void enter () {
         if ( shore != null ) shore.stop();
      }
   }
}
