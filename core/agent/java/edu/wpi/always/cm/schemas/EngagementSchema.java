package edu.wpi.always.cm.schemas;

import java.util.Arrays;
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

   public EngagementSchema (BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory,
         EngagementPerceptor engagementPerceptor, SchemaManager schemaManager) {
      super(behaviorReceiver, behaviorHistory);
      this.engagementPerceptor = engagementPerceptor;
      this.schemaManager = schemaManager;
   }

   private EngagementState lastState;

   private boolean dim = true;
   
   private boolean started; // session started
   
   @Override
   public void run () {
      // needs to have higher priority than session schema
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(ActivitySchema.SPECIFICITY+0.4)
            .build();
      EngagementPerception engagementPerception = engagementPerceptor
            .getLatest();
      if ( engagementPerception != null ) {
         switch (engagementPerception.getState()) {
            //TODO: Need to make it *very* hard to end session!!!!
            case Idle:
               if ( lastState != EngagementState.Idle ) {
                  // TODO: need to end session
               }
               if ( !dim) {
                  // TODO: dim screen
                  java.awt.Toolkit.getDefaultToolkit().beep();
                  dim = true;
               }
               propose(Behavior.newInstance(new IdleBehavior(false)), m);
               break;
            case Attention:
               undim();
               proposeNothing();
               break;
            case Initiation:
               undim();
               propose(Behavior.newInstance(new SpeechBehavior("Hi"), new MenuBehavior(Arrays.asList("Hi"))), m);
               break;
            case Engaged:
               undim();
               if ( !started ) { 
                  Utils.lnprint(System.out, "Starting session...");
                  schemaManager.start(SessionSchema.class);
                  started = true;
               }
               proposeNothing();
               break;
            case Recovering:
               undim();
               propose(Behavior.newInstance(new SpeechBehavior("Are you still there?"), new MenuBehavior(
                     Arrays.asList("Yes"))), m);
               break;
         }
         lastState = engagementPerception.getState();
      } else
         lastState = null;
   }
   
   private void undim () {
      if ( dim ) {
         // TODO: Un-dim screen
         java.awt.Toolkit.getDefaultToolkit().beep();
         try { Thread.sleep(250); } catch (InterruptedException e) {}
         java.awt.Toolkit.getDefaultToolkit().beep();
         dim = false;
      }
   }
}
