package edu.neu.always.skype;

import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

// this is the schema for initiating a Skype call

public class SkypeSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {

   private final ShoreFacePerceptor shore;
   
   public SkypeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ShoreFacePerceptor shore) {
      super(new Test(shore),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
   }

   @Override
   public void dispose () { 
      // this is here so it is run even if schema throws an error
      if ( shore != null ) shore.start(); 
   }
 
   // to test camera release and restart
   
   public static class Test extends MultithreadAdjacencyPair<AdjacencyPair.Context> {

      private final ShoreFacePerceptor shore;
      
      public Test (ShoreFacePerceptor shore) {
         super("I am taking the camera until you say ok", new AdjacencyPair.Context());
         this.shore = shore;
         choice("Ok", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () { 
               getContext().getSchema().stop();
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
