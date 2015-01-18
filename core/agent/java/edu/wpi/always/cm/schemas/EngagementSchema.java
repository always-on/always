package edu.wpi.always.cm.schemas;

import java.util.Arrays;
import edu.wpi.always.*;
import edu.wpi.always.Always.AgentType;
import edu.wpi.always.client.*;
import edu.wpi.always.client.reeti.ReetiCommandSocketConnection;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuBehavior;
import edu.wpi.disco.rt.schema.*;
import edu.wpi.disco.rt.util.Utils;

public class EngagementSchema extends SchemaBase {

   private final EngagementPerceptor engagementPerceptor;
   private final SchemaManager schemaManager;
   private final ClientProxy proxy;
   private final CollaborationManager cm;
   private ReetiCommandSocketConnection reeti;

   public EngagementSchema (BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory,
         EngagementPerceptor engagementPerceptor, SchemaManager schemaManager, 
         ClientProxy proxy, CollaborationManager cm) {
      super(behaviorReceiver, behaviorHistory);
      this.engagementPerceptor = engagementPerceptor;
      this.schemaManager = schemaManager;
      this.proxy = proxy;
      this.cm = cm;
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

   public static volatile boolean EXIT; // set by other schemas
   
   @Override
   public void run () {
      try {
         EngagementPerception engagementPerception = engagementPerceptor.getLatest();
         // socket not initialized until after this schema started
         reeti = cm.getReetiSocket();
         if ( engagementPerception != null ) {
            switch (state = (EXIT ? EngagementState.IDLE : engagementPerception.getState())) {
               case IDLE:
                  if ( EXIT || lastState == EngagementState.RECOVERING ) {
                     if ( EXIT && lastState != state && lastState != EngagementState.RECOVERING
                           && lastState != EngagementState.ENGAGED )
                        Logger.THIS.logEngagement(lastState, state);
                     // if ( reeti != null ) reeti.reboot(); 
                     Always.exit(0);
                  } 
                  if ( !EXIT && lastState != EngagementState.IDLE ) { 
                     proxy.setAgentVisible(false);
                     propose(HELLO, META);
                  }
                  break;
               case ATTENTION:
                  if ( started ) proposeNothing();
                  else {
                     propose(HI, META);
                     visible();
                     if ( reeti != null && lastState != EngagementState.ATTENTION) 
                        reeti.wiggleEars();
                  }
                  break;
               case INITIATION:
                  if ( started ) proposeNothing();
                  else {
                     visible();
                     propose(HI_HI, META);
                  }
                  break;
               case ENGAGED:
                  if ( !started ) { 
                     Utils.lnprint(System.out, "Starting session...");
                     schemaManager.start(SessionSchema.class);
                     started = true;
                  }
                  visible();
                  proposeNothing();
                  break;
               case RECOVERING:
                  visible();
                  propose(Behavior.newInstance(new SpeechBehavior("Are you still there?"), 
                        new MenuBehavior(Arrays.asList("Yes, I'm here"))), META);
                  break;
            }
            lastState = state;
         }
      } catch (Exception e) {
         e.printStackTrace();
         Always.exit(2);
      }
   }
      
   private void visible () {
      if ( lastState != state ) proxy.setScreenVisible(true);
      if ( Always.getAgentType() != AgentType.REETI ) 
         proxy.setAgentVisible(true);
   }
}
