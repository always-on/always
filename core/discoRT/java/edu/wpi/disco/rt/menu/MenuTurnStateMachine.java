package edu.wpi.disco.rt.menu;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.behavior.Constraint.Type;
import edu.wpi.disco.rt.realizer.petri.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.util.List;

public class MenuTurnStateMachine implements BehaviorBuilder {

   public static final int TIMEOUT_DELAY = 10000; 
   public static final int MENU_DELAY = 10;
   
   private final BehaviorHistory behaviorHistory;
   private final MenuPerceptor menuPerceptor;
   private final ResourceMonitor resourceMonitor;
   private final MenuTimeoutHandler timeoutHandler;

   private TimeStampedValue<Behavior> previousBehavior = new TimeStampedValue<Behavior>(Behavior.NULL);
   private AdjacencyPair state, previousState;
   private Mode mode;
   private DateTime waitingForResponseSince;
   private boolean needsToDifferentiate;
   private boolean extension, needsFocusResource;
   
   public AdjacencyPair getState () { return state; }
   
   public void setExtension (boolean extension) { this.extension = extension; }
   
   @Override
   public void setNeedsFocusResource (boolean focus) {
      this.needsFocusResource = focus;
   }

   private enum Mode { Speaking, Hearing }  // mode of agent

   public MenuTurnStateMachine (BehaviorHistory behaviorHistory,
         ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor,
         MenuTimeoutHandler timeoutHandler) {
      this.behaviorHistory = behaviorHistory;
      this.resourceMonitor = resourceMonitor;
      this.menuPerceptor = menuPerceptor;
      this.timeoutHandler = timeoutHandler;
      setMode(Mode.Speaking);
   }
 
   @Override
   public Behavior build () {
      // note this method is coded as tail-recursive loops
      if ( state == null ) {
         if ( DiscoRT.TRACE) System.out.println("Nothing to say/do");
         return Behavior.NULL;
      }
      if ( !hasSomethingToSay(state) && !hasChoicesForUser(state) ) {
         setState(state.nextState(null));
         return Behavior.NULL;
      }
      
      if ( state.prematureEnd() ) return nextState(null); // loop
      Behavior behavior = Behavior.NULL;
      MenuBehavior menuBehavior = hasChoicesForUser(state) ? 
         new MenuBehavior(state.getChoices(), state.isTwoColumnMenu(), extension) :
         null;
      SpeechBehavior speechBehavior = hasSomethingToSay(state) ? 
         new SpeechBehavior(state.getMessage()) : null;
      if ( speechBehavior != null && menuBehavior != null ) {
         behavior = new Behavior(new CompoundBehaviorWithConstraints(
                Lists.newArrayList(speechBehavior, menuBehavior),
                Lists.newArrayList(new Constraint(
                      new SyncRef(SyncPoint.Start, speechBehavior), 
                      new SyncRef(SyncPoint.Start, menuBehavior),
                      Type.After, MENU_DELAY))));
      } else if ( mode == Mode.Speaking ) {
         if ( speechBehavior == null ) {
            setMode(Mode.Hearing);
            return build(); // loop
         }
         behavior = Behavior.newInstance(speechBehavior);
      } else if ( mode == Mode.Hearing ) {
         if ( menuBehavior == null ) return nextState(null); // loop
         behavior = Behavior.newInstance(menuBehavior);
         waitingForResponseSince = DateTime.now(); // reset now since nothing said
      }
      if ( needsFocusResource ) behavior = behavior.addFocusResource(); 
      if ( previousState != state ) {
         if ( previousBehavior.getValue().equals(behavior) ) needsToDifferentiate = true;
         update(Behavior.NULL);
      }
      if ( needsToDifferentiate ) {
         // this is a bit of a hack to fix problem that if two successive
         // states produce the same utterance, it won't be said twice because
         // the behavior history thinks it is already done
         CompoundBehavior inner = behavior.getInner();
         behavior = new Behavior(new SequenceOfCompoundBehaviors(inner,
               // make null behavior that uses same resource as inner
               new SimpleCompoundBehavior(PrimitiveBehavior.nullBehavior(inner.getResources().iterator().next()))));
      }
      // check if already done and update behavior
      boolean alreadyDone = false;
      if ( previousBehavior.getValue().equals(behavior) ) {
         if ( behaviorHistory.isDone(behavior.getInner(), previousBehavior.getTimeStamp()) ) 
            alreadyDone = true;
      } else update(behavior);
      if ( alreadyDone && mode == Mode.Speaking ) setMode(Mode.Hearing);
      if ( menuBehavior != null
           && resourceMonitor.isDone(menuBehavior, previousBehavior.getTimeStamp()) ) {
         String selected = checkMenuSelected(state.getChoices(),
                                             previousBehavior.getTimeStamp());
         if ( selected != null ) {
            // prevent infinite loop when same state, same menu and no message
            if ( state == previousState ) update(Behavior.NULL);
            return nextState(selected); // loop
         }
      }
      if ( mode == Mode.Hearing && !extension
           && waitingForResponseSince.isBefore(DateTime.now().minusMillis(TIMEOUT_DELAY)) ) {
         AdjacencyPair newState = timeoutHandler.handle(state);
         if ( newState != null && newState != state ) {
            setState(newState);
            return build(); // loop
         }
      }
      return behavior;
   }

   private boolean hasSomethingToSay (AdjacencyPair state) {
      String s = state.getMessage();
      return s != null && s.length() > 0;
   }

   private boolean hasChoicesForUser (AdjacencyPair state) {
      return state.getChoices() != null && !state.getChoices().isEmpty();
   }
   
   private String checkMenuSelected (List<String> userChoices,
         DateTime menuShownAt) {
      MenuPerception p = menuPerceptor.getLatest();
      // ignore selection that is not in choices, since could be from 
      // menu extension (or vice versa)
      if ( p != null && userChoices.contains(p.getSelected())
         && p.getTimeStamp().isAfter(menuShownAt) ) {
         return p.getSelected();
      }
      return null;
   }

   private void update (Behavior b) {
      previousState = state;
      previousBehavior = new TimeStampedValue<Behavior>(b);
   }

   private Behavior nextState (String text) {
      setState(state.nextState(text));
      return build(); // loop
   }
   
   public void setState (AdjacencyPair newState) {
      setMode(Mode.Speaking);
      needsToDifferentiate = false;
      if ( newState == null ) return;
      state = newState;
      newState.enter();
   }

   private double specificity;
   private boolean newActivity;

   @Override
   public BehaviorMetadata getMetadata () {
      if ( state == null )
         return new BehaviorMetadataBuilder().build();
      BehaviorMetadataBuilder builder = new BehaviorMetadataBuilder()
            .specificity(specificity)
            .timeRemaining(state.timeRemaining())
            .newActivity(newActivity);
      return builder.build();
   }

   public void setNewActivity (boolean n) {
      this.newActivity = n;
   }

   public void setSpecificityMetadata (double s) {
      this.specificity = s;
   }

   private void setMode (Mode newMode) {
      if ( newMode == Mode.Hearing && mode == Mode.Speaking )
         waitingForResponseSince = DateTime.now();
      this.mode = newMode;
   }
}
