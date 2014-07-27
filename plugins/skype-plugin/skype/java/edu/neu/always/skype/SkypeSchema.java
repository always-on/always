package edu.neu.always.skype;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

abstract class SkypeSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {
   
   public final static Logger.Activity LOGGER_NAME = Logger.Activity.SKYPE;
   public enum Direction { INCOMING, OUTGOING }
   
   protected static void log (Direction direction, int duration) {
      Logger.logActivity(LOGGER_NAME, direction, duration, SkypeInterruptHandler.CALLER_NAME);
   }
     
   protected final FacePerceptor shore;
   protected final UIMessageDispatcher dispatcher;
   
   protected SkypeSchema (AdjacencyPair initial, BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, FacePerceptor shore, UIMessageDispatcher dispatcher, 
         SkypeClient client, Always always) {
      super(initial,
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            LOGGER_NAME);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
      this.dispatcher = dispatcher;
      // note client not used here, but must be argument to force creation
      always.getUserModel().setProperty(SkypePlugin.PERFORMED, true);
      interruptible = false;
      setSelfStop(true);
      EXIT = false;
   }
   
   static boolean EXIT;  // See SkypeClient

   @Override
   public void runActivity () {
      if ( EXIT ) stop();
      else super.runActivity();
   }

   @Override
   public void stop () {
      super.stop();
      EXIT = false;
   }
   
   @Override
   public void dispose () { 
      super.dispose();      
      // this code is here so it is run even if schema throws an error
      if ( shore != null ) shore.start();
      dispatcher.send(new Message("endCall"));
      EngagementPerception.setRecoveringEnabled(true);
      EXIT = false;
   }
   
   static class SkypeStop extends MultithreadAdjacencyPair<AdjacencyPair.Context> {
      
      SkypeStop (Context context) { 
         super("Done", context);
         this.repeatOption = false;
      }
      
      @Override
      public void enter () { getContext().getSchema().stop(); }
   }

}
