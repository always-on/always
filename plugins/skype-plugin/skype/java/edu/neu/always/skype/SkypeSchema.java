package edu.neu.always.skype;

import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.schema.Schema;

// this is the schema for initiating a Skype call

public class SkypeSchema extends ActivityStateMachineSchema {

   private final ShoreFacePerceptor shore;
   
   public SkypeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ShoreFacePerceptor shore) {
      super(null, behaviorReceiver, behaviorHistory, resourceMonitor,
				menuPerceptor);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
      stateMachine.setState(new Test(shore, this));
   }

   @Override
   public void dispose () { 
      // this is here so it is run even if schema throws an error
      if ( shore != null ) shore.start(); 
   }
 
   // to test camera release and restart
   
   public static class Test extends MultithreadAdjacencyPair<Void> {

      private final ShoreFacePerceptor shore;
      
      public Test (ShoreFacePerceptor shore, final Schema schema) {
         super("This is a test", null);
         this.shore = shore;
         choice("Ok", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () { 
               schema.cancel();
               return null;
            }
         });
      }
      
      @Override
      public void enter () {
         if ( shore != null ) shore.stop();
      }
   }
}
