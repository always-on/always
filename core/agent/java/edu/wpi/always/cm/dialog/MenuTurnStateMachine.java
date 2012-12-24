package edu.wpi.always.cm.dialog;

import com.google.common.collect.Lists;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;
import edu.wpi.always.cm.realizer.Constraint.Type;
import org.joda.time.DateTime;
import java.util.List;

public class MenuTurnStateMachine implements BehaviorBuilder {

   private static final int TIMEOUT_DELAY = 10000;
   private static final int MENU_DELAY = 10;
   private final BehaviorHistory behaviorHistory;
   private final MenuPerceptor menuPerceptor;
   private AdjacencyPair currentAdjacencyPair = null;
   private State state;
   private DateTime waitingForResponseSince;
   private TimeStampedValue<Behavior> lastProposal = new TimeStampedValue<Behavior>(
         Behavior.NULL);
   private AdjacencyPair stateOfLastProposal;
   private final ResourceMonitor resourceMonitor;
   private boolean addSomethingToDifferentiateFromLastProposal;
   private final MenuTimeoutHandler timeoutHandler;

   private enum State {
      Say, Hear
   }

   public MenuTurnStateMachine (BehaviorHistory behaviorHistory,
         ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor,
         MenuTimeoutHandler timeoutHandler) {
      this.behaviorHistory = behaviorHistory;
      this.resourceMonitor = resourceMonitor;
      this.menuPerceptor = menuPerceptor;
      this.timeoutHandler = timeoutHandler;
      currentAdjacencyPair = null;
      setState(State.Say);
   }

   private boolean hasSomethingToSay (AdjacencyPair pair) {
      String s = pair.getMessage();
      return s != null && s.length() > 0;
   }

   private boolean hasChoicesForUser (AdjacencyPair pair) {
      return pair.getChoices() != null && !pair.getChoices().isEmpty();
   }

   @Override
   public Behavior build () {
      if ( currentAdjacencyPair == null ) {
         System.out.println("Nothing to say/do");
         return Behavior.NULL;
      }
      if ( !hasSomethingToSay(currentAdjacencyPair)
         && !hasChoicesForUser(currentAdjacencyPair) ) {
         AdjacencyPair nextState = currentAdjacencyPair.nextState(null);
         setAdjacencyPair(nextState);
         return Behavior.NULL;
      }
      if ( currentAdjacencyPair.prematureEnd() )
         return gotoSaying(null);
      List<String> userChoices = currentAdjacencyPair.getChoices();
      boolean choicesExist = hasChoicesForUser(currentAdjacencyPair);
      MenuBehavior menuBehavior = null;
      SpeechBehavior speechBehavior = null;
      Behavior b = Behavior.NULL;
      if ( choicesExist )
         menuBehavior = new MenuBehavior(userChoices,
               currentAdjacencyPair.isTwoColumnMenu());
      if ( hasSomethingToSay(currentAdjacencyPair) )
         speechBehavior = new SpeechBehavior(currentAdjacencyPair.getMessage());
      if ( speechBehavior != null && menuBehavior != null ) {
         List<PrimitiveBehavior> primitives = Lists.newArrayList(
               speechBehavior, menuBehavior);
         List<Constraint> constraints = Lists.newArrayList();
         constraints.add(new Constraint(new SyncRef(SyncPoint.Start,
               speechBehavior), new SyncRef(SyncPoint.Start, menuBehavior),
               Type.After, MENU_DELAY));
         b = new Behavior(new CompoundBehaviorWithConstraints(primitives,
               constraints));
      } else if ( state == State.Say ) {
         if ( speechBehavior == null ) {
            setState(State.Hear);
            return build();
         }
         b = Behavior.newInstance(speechBehavior);
      } else if ( state == State.Hear ) {
         if ( menuBehavior == null )
            return gotoSaying(null);
         b = Behavior.newInstance(menuBehavior);
      }
      if ( stateOfLastProposal != currentAdjacencyPair ) {
         if ( lastProposal.getValue().equals(b) )
            addSomethingToDifferentiateFromLastProposal = true;
         setLastProposal(Behavior.NULL);
      }
      if ( addSomethingToDifferentiateFromLastProposal )
         b = new Behavior(new SequenceOfCompoundBehaviors(
               new SimpleCompoundBehavior(new SpeechBehavior(".")),
               b.getInner()));
      boolean alreadyDone = saveProposalAndCheckIfAlreadyDone(b);
      if ( alreadyDone && state == State.Say )
         setState(State.Hear);
      if ( menuBehavior != null
         && resourceMonitor.isDone(menuBehavior, lastProposal.getTimeStamp()) ) {
         String selectedMenu = checkMenuSelected(userChoices,
               lastProposal.getTimeStamp());
         if ( selectedMenu != null )
            return gotoSaying(selectedMenu);
      }
      if ( state == State.Hear
         && waitingForResponseSince.isBefore(DateTime.now().minusMillis(
               TIMEOUT_DELAY)) ) {
         AdjacencyPair newPair = timeoutHandler.handle(currentAdjacencyPair);
         if ( newPair != null && newPair != currentAdjacencyPair ) {
            setAdjacencyPair(newPair);
            return build();
         }
      }
      return b;
   }

   private String checkMenuSelected (List<String> userChoices,
         DateTime menuShownAt) {
      MenuPerception p = menuPerceptor.getLatest();
      if ( p != null && userChoices.contains(p.selectedMenu())
         && p.getTimeStamp().isAfter(menuShownAt) ) {
         return p.selectedMenu();
      }
      return null;
   }

   private boolean saveProposalAndCheckIfAlreadyDone (Behavior b) {
      if ( lastProposal.getValue().equals(b) ) {
         if ( behaviorHistory.isDone(b.getInner(), lastProposal.getTimeStamp()) ) {
            return true;
         }
      } else {
         setLastProposal(b);
      }
      return false;
   }

   private void setLastProposal (Behavior b) {
      stateOfLastProposal = currentAdjacencyPair;
      lastProposal = new TimeStampedValue<Behavior>(b);
   }

   private Behavior gotoSaying (String text) {
      setAdjacencyPair(currentAdjacencyPair.nextState(text));
      return build();
   }

   public void setAdjacencyPair (AdjacencyPair pair) {
      setState(State.Say);
      addSomethingToDifferentiateFromLastProposal = false;
      if ( pair == null )
         return;
      currentAdjacencyPair = pair;
      pair.enter();
   }

   private double specificity;
   private boolean newActivity;

   @Override
   public BehaviorMetadata getMetadata () {
      if ( currentAdjacencyPair == null )
         return new BehaviorMetadataBuilder().build();
      BehaviorMetadataBuilder builder = new BehaviorMetadataBuilder()
            .specificity(specificity)
            .timeRemaining(currentAdjacencyPair.timeRemaining())
            .newActivity(newActivity);
      return builder.build();
   }

   public void setNewActivity (boolean n) {
      this.newActivity = n;
   }

   public void setSpecificityMetadata (double s) {
      this.specificity = s;
   }

   public State getState () {
      return state;
   }

   private void setState (State newState) {
      if ( newState == State.Hear && state == State.Say )
         waitingForResponseSince = DateTime.now();
      this.state = newState;
   }
}
