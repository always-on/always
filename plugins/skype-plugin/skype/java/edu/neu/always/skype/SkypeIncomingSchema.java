package edu.neu.always.skype;

import edu.wpi.always.Always;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class SkypeIncomingSchema extends SkypeSchema {
   
   /* TODO for logging:
    * 
    * Note: If you are satisfied with the log messages that are already
    * automatically generated for start/end of activity and for all
    * user model updates, then you can delete the log method below
    * (and already defined enums above, if any) and go directly to (4) below.
    *
    * (1) Add arguments to log method in superclass as needed (use enums instead of
    *     string constants to avoid typos and ordering errors!)
    *     
    * (2) Update always/docs/log-format.txt with any new logging fields
    * 
    * (3) Call log method at appropriate places in code
    * 
    * (4) Remove this comment!
    *
    */
   public static void log (int duration) { log(Direction.INCOMING, duration); }
   
   public SkypeIncomingSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, FacePerceptor shore, UIMessageDispatcher dispatcher, 
         SkypeClient client, Always always) {
      super(new IncomingSkype(shore, dispatcher),
            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor,
            shore, dispatcher, client, always);
      SessionSchema.startInterruption();
   }
   
   private static class IncomingSkype extends MultithreadAdjacencyPair<AdjacencyPair.Context> {
   
      protected final FacePerceptor shore;
      protected final UIMessageDispatcher dispatcher;

      private IncomingSkype (FacePerceptor shore, UIMessageDispatcher dispatcher) {
         super("", new AdjacencyPair.Context());
         this.shore = shore;
         this.dispatcher = dispatcher;
         this.repeatOption = false;
         choice("Please end this call", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new SkypeStop(getContext());
            }});
      }
            
      @Override
      public void enter () {
         if ( shore != null ) shore.stop();
         dispatcher.send(new Message("acceptCall"));
         // NB: Safer to move following to someplace where it is only called 
         // once the video connection is successfully established!!!
         EngagementPerception.setRecoveringEnabled(false);
      }
   }
}
