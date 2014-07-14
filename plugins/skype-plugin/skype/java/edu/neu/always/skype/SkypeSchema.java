package edu.neu.always.skype;

import com.google.gson.JsonObject;

import edu.wpi.always.*;
import edu.wpi.always.client.Message;
import edu.wpi.always.client.MessageHandler;
import edu.wpi.always.client.SkypeInterruptHandler;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.cm.perceptors.EngagementPerception;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

// this is the schema for initiating a Skype call

public class SkypeSchema extends ActivityStateMachineSchema<AdjacencyPair.Context> {

   public final static Logger.Activity LOGGER_NAME = Logger.Activity.SKYPE;
   public enum Direction { INCOMING, OUTGOING }
   public enum Appointment { APPOINTMENT, NOT_APPOINTMENT }
   
   /* TODO for logging:
    * 
    * Note: If you are satisfied with the log messages that are already
    * automatically generated for start/end of activity and for all
    * user model updates, then you can delete the log method below
    * (and already defined enums above, if any) and go directly to (4) below.
    *
    * (1) Add arguments to log method below as needed (use enums instead of
    *     string constants to avoid typos and ordering errors!)
    *     
    * (2) Update always/docs/log-format.txt with any new logging fields
    * 
    * (3) Call log method at appropriate places in code
    * 
    * (4) Remove this comment!
    *
    */
   public static void log (Direction direction, Appointment appointment, int duration) {
      Logger.logActivity(LOGGER_NAME, direction, appointment, duration, SkypeInterruptHandler.CALLER_NAME);
   }
     
   private final ShoreFacePerceptor shore;
   
   public SkypeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, ShoreFacePerceptor shore, UIMessageDispatcher dispatcher, Always always) {
      super(new AcceptCall(shore,dispatcher),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            LOGGER_NAME);
      this.shore = shore instanceof ShoreFacePerceptor.Reeti ? null : shore;
      always.getUserModel().setProperty(SkypePlugin.PERFORMED, true);
      interruptible = false;
      SessionSchema.startInterruption();
      // NB: Safer to move this to someplace where it is only called 
      //     once the video connection is established!!!
      EngagementPerception.setRecoveringEnabled(false);
   }
   
   @Override
   public void dispose () { 
      super.dispose();      
      // these are here so it is run even if schema throws an error
      if ( shore != null ) shore.start();
      EngagementPerception.setRecoveringEnabled(true);
   }
   
   // to test camera release and restart
   
   public static class AcceptCall extends MultithreadAdjacencyPair<AdjacencyPair.Context> {

      private final ShoreFacePerceptor shore;
      private final UIMessageDispatcher dispatcher;

      public AcceptCall (ShoreFacePerceptor shore,final UIMessageDispatcher dispatcher) {
         super("", new AdjacencyPair.Context());
         this.shore = shore;
         this.dispatcher = dispatcher;
         dispatcher.registerReceiveHandler("callEnded", new MessageHandler() {
             @Override
             public void handleMessage (JsonObject body) {
                 //dispatcher.send(new Message("endCall"));
                 getContext().getSchema().stop();
             }
          });
         
         choice("EndCall", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () { 
               dispatcher.send(new Message("endCall"));
               getContext().getSchema().stop();
               return null;
            }
         });
      }
      
      @Override
      public void enter () {
         if ( shore != null ) shore.stop();
         dispatcher.send(new Message("acceptCall"));
      }
      
      public void onCallEnd(){
    	  
      }
   }
}
