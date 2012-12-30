package edu.wpi.always.cm.schemas;

import com.google.common.collect.Lists;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.schema.SchemaBase;
import java.util.*;

public class SimpleGreetingsSchema extends SchemaBase implements
      DialogContentProvider {

   private final FacePerceptor facePerceptor;
   private State state = State.Init;
   private final EngagementPerceptor engagementPerceptor;
   private OldDialogStateMachine stateMachine;

   public SimpleGreetingsSchema (BehaviorProposalReceiver behaviorReceiver,
         final BehaviorHistory resourceMonitor, FacePerceptor facePerceptor,
         MenuPerceptor menuPerceptor,
         EngagementPerceptor engagementPerceptor) {
      super(behaviorReceiver, resourceMonitor);
      this.facePerceptor = facePerceptor;
      this.engagementPerceptor = engagementPerceptor;
      BehaviorHistory historyWithFocusRequestAugmenter = behaviorHistoryWithAutomaticInclusionOfFocus();
      stateMachine = new OldDialogStateMachine(
            historyWithFocusRequestAugmenter, this, menuPerceptor);
      stateMachine.setSpecificityMetadata(.9);
      setNeedsFocusResouce();
   }

   @Override
   public void run () {
      EngagementPerception engPerception = engagementPerceptor
            .getLatest();
      if ( state == State.Done
         && (engPerception == null || !engPerception.isEngaged()) ) {
         changeStateTo(State.Init);
      }
      propose(stateMachine);
   }

   private ArrayList<String> getUserGreetingMenuItems () {
      return Lists.newArrayList("Hi", "Hello", "Good morning");
   }

   private void changeStateTo (State s) {
      state = s;
      if ( state == State.Done ) {
         engagementPerceptor.setEngaged(true);
      }
   }

   private enum State {
      Init, WaitingForResponse, Done
   }

   @Override
   public String whatToSay () {
      if ( state == State.Init ) {
         FacePerception f = facePerceptor.getLatest();
         if ( f != null && f.getPoint() != null ) {
            return "Hi";
         }
      }
      return null;
   }

   @Override
   public void doneSaying (String text) {
      changeStateTo(State.WaitingForResponse);
   }

   @Override
   public List<String> userChoices () {
      if ( state == State.WaitingForResponse ) {
         return getUserGreetingMenuItems();
      }
      return null;
   }

   @Override
   public void userSaid (String text) {
      changeStateTo(State.Done);
   }

   @Override
   public double timeRemaining () {
      double t;
      switch (state) {
      case Init:
         t = 1;
         break;
      case WaitingForResponse:
         t = 0.5;
         break;
      default:
         t = 0;
      }
      return t;
   }
}
