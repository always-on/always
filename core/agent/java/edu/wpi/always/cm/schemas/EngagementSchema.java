package edu.wpi.always.cm.schemas;

import java.util.Arrays;
import com.sun.msv.datatype.xsd.Proxy;
import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.ClientProxy;
import edu.wpi.always.cm.perceptors.EngagementPerception;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.cm.perceptors.EngagementPerceptor;
import edu.wpi.always.cm.perceptors.FacePerception;
import edu.wpi.always.cm.perceptors.FacePerceptor;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.always.cm.primitives.IdleBehavior;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.Behavior;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import edu.wpi.disco.rt.behavior.BehaviorMetadataBuilder;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;
import edu.wpi.disco.rt.behavior.SpeechBehavior;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import edu.wpi.disco.rt.schema.SchemaManager;
import edu.wpi.disco.rt.util.Utils;

public class EngagementSchema extends SchemaBase {

   private final EngagementPerceptor engagementPerceptor;
   private final SchemaManager schemaManager;
   private final ClientProxy proxy;

   public EngagementSchema (BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory,
         EngagementPerceptor engagementPerceptor, SchemaManager schemaManager, ClientProxy proxy) {
      super(behaviorReceiver, behaviorHistory);
      this.engagementPerceptor = engagementPerceptor;
      this.schemaManager = schemaManager;
      this.proxy = proxy;
   }

   private EngagementState state, lastState;

   private boolean started; // session started
   
   // needs to have higher priority than session schema
   private final static BehaviorMetadata META = 
         new BehaviorMetadataBuilder().specificity(ActivitySchema.SPECIFICITY+0.4)
            .build();
 
   // optimized to reduce GC for long running
   private final static MenuBehavior HELLO = new MenuBehavior(Arrays.asList("Hello"));
   
   private final static MenuBehavior HI = new MenuBehavior(Arrays.asList("Hi"));

   private final static Behavior HI_HI = Behavior.newInstance(new SpeechBehavior("Hi"), HI);

   @Override
   public void run () {
      EngagementPerception engagementPerception = engagementPerceptor.getLatest();
      if ( engagementPerception != null ) {
         switch (state = engagementPerception.getState()) {
            case Idle:
               if ( lastState == EngagementState.Recovering ) { 
                  Utils.lnprint(System.out, "EXITING FOR IDLE...");
                  System.exit(0); 
               } 
               if ( lastState != EngagementState.Idle ) { 
                  proxy.setAgentVisible(false);
                  propose(HELLO, META);
               }
               break;
            case Attention:
               if ( started ) proposeNothing();
               else {
                  propose(HI, META);
                  visible();
               }
               break;
            case Initiation:
               if ( started ) proposeNothing();
               else {
                  visible();
                  propose(HI_HI, META);
               }
               break;
            case Engaged:
               if ( !started ) { 
                  Utils.lnprint(System.out, "Starting session...");
                  schemaManager.start(SessionSchema.class);
                  started = true;
               }
               visible();
               proposeNothing();
               break;
            case Recovering:
               visible();
               propose(Behavior.newInstance(new SpeechBehavior("Are you still there?"), 
                                            new MenuBehavior(Arrays.asList("Yes"))), META);
               break;
         }
         lastState = engagementPerception.getState();
      }
   }
      
   private void visible () {
      if ( lastState != state ) proxy.setScreenVisible(true);
      if ( Always.getAgentType() != AgentType.Reeti ) 
         proxy.setAgentVisible(true);
   }
}
