package edu.neu.always.skype;

import edu.wpi.always.Always;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class SkypeIncomingSchema extends SkypeSchema {

   protected final FacePerceptor shore;
   protected final UIMessageDispatcher dispatcher;
   
   public SkypeIncomingSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, FacePerceptor shore, UIMessageDispatcher dispatcher, 
         Always always, SkypeClient client) {
      super(new IncomingSkype(shore, dispatcher),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
      this.dispatcher = dispatcher;
      // note client not used, but must be in argument list for creation
      interruptible = false;
      EXIT = false;
   }
   
   // TODO  ***DANGER** After sending endCall message below
   //       system will wait indefinitely until it receives
   //       callEnded from client (so that Shore can restart camera)
   //       If client crashes at this point, system is permanently
   //       hung.  Solution???
   
   static volatile boolean EXIT; // set from SkypeClient thread
   
   @Override
   public void runActivity () {
      if ( EXIT ) stop();
      else super.runActivity();
   }
   
   @Override
   public void dispose () { 
      super.dispose();      
      // this code is here so it is run even if schema throws an error
      if ( shore != null ) shore.start();
      EngagementPerception.setRecoveringEnabled(true);
   }
   
   private static class IncomingSkype extends MultithreadAdjacencyPair<AdjacencyPair.Context> {
   
      protected final FacePerceptor shore;
      protected final UIMessageDispatcher dispatcher;

      private IncomingSkype (FacePerceptor shore, final UIMessageDispatcher dispatcher) {
         super("", new AdjacencyPair.Context());
         this.shore = shore;
         this.dispatcher = dispatcher;
         this.repeatOption = false;
         choice("Please end this call", new DialogStateTransition() {
             @Override
             public AdjacencyPair run () { 
                dispatcher.send(new Message("endCall"));
                return null;
             }
         });
      }
            
      @Override
      public void enter () {
         log(Direction.INCOMING, SkypeInterruptHandler.CALLER_NAME);
         if ( shore != null ) shore.stop();
         dispatcher.send(new Message("acceptCall"));
         // NB: Safer to move following to someplace where it is only called 
         // once the video connection is successfully established!!!
         EngagementPerception.setRecoveringEnabled(false);
      }
   }
}
