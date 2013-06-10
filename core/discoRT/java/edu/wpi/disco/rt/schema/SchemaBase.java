package edu.wpi.disco.rt.schema;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.util.concurrent.ScheduledFuture;

//The propose family of methods, append focus resource request based on
//  needsFocusResource field.

public abstract class SchemaBase implements Schema {

   public static int NORMAL_PRIORITY = 0;
   public static boolean backchanneling = false; // for story, temp?
   
   private final BehaviorProposalReceiver behaviorReceiver;
   private final BehaviorHistory behaviorHistory;
   
   private TimeStampedValue<Behavior> lastProposal;
   private boolean needsFocusResource;
   private ScheduledFuture<?> future;
   
   @Override
   public void setFuture (ScheduledFuture<?> future) { this.future = future; }

   protected SchemaBase (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory) {
      this.behaviorReceiver = behaviorReceiver;
      this.behaviorHistory = behaviorHistory;
      lastProposal = new TimeStampedValue<Behavior>(Behavior.NULL);
   }
 
   @Override
   public void cancel () { 
      if ( future != null ) future.cancel(false);
   }
 
   @Override
   public boolean isDone () {
      return future == null || future.isDone();
   }
   
   private long focusMillis;
   
   @Override
   public void focus () {
      focusMillis = System.currentTimeMillis();
   }

   /**
    * Return number of milliseconds since this schema's proposal including
    * focus was most recently chosen by arbitrator (or -1 if never).
    * NB: Do not assume this schema currently has the focus.
    */
   protected long getFocusMillis () {
      return focusMillis == 0 ? -1 : (System.currentTimeMillis() - focusMillis); 
   }
   
   protected void proposeNothing () {
      propose(Behavior.NULL, 0);
   }

   protected void propose (PrimitiveBehavior behavior, double specificity) {
      propose(Behavior.newInstance(behavior), specificity);
   }

   protected void propose (PrimitiveBehavior behavior, BehaviorMetadata metadata) {
      propose(Behavior.newInstance(behavior), metadata);
   }

   protected void propose (Behavior behavior, double specificity) {
      BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(
            specificity).build();
      propose(behavior, m);
   }

   protected void propose (Behavior behavior, BehaviorMetadata metadata) {
      if ( !behavior.equals(Behavior.NULL) )
         behavior = appendFocusRequestIfNecessary(behavior);
      updateLastProposal(behavior);
      behaviorReceiver.add(this, behavior, metadata);
   }

   protected Behavior appendFocusRequestIfNecessary (Behavior behavior) {
      return new Behavior(appendFocusRequestIfNecessary(behavior.getInner()));
   }

   protected CompoundBehavior appendFocusRequestIfNecessary (
         CompoundBehavior behavior) {
      return needsFocusResource ?
         new SequenceOfCompoundBehaviors(behavior,
               new SimpleCompoundBehavior(new FocusRequestBehavior()))
         : behavior;
   }

   private void updateLastProposal (Behavior behavior) {
      if ( !behavior.equals(lastProposal) ) {
         lastProposal = new TimeStampedValue<Behavior>(behavior);
      }
   }

   protected boolean lastProposalIsDone () {
      TimeStampedValue<Behavior> p = this.lastProposal;
      if ( p.getValue() != Behavior.NULL
         && behaviorHistory.isDone(p.getValue().getInner(), p.getTimeStamp()) )
         return true;
      return false;
   }

   protected TimeStampedValue<Behavior> getLastProposal () {
      return lastProposal;
   }

   protected void propose (BehaviorBuilder builder) {
      propose(builder.build(), builder.getMetadata());
   }

   protected boolean getNeedsFocusResource () {
      return needsFocusResource;
   }

   protected void setNeedsFocusResource (boolean val) {
      this.needsFocusResource = val;
   }

   protected BehaviorHistory behaviorHistoryWithAutomaticInclusionOfFocus () {
      return new BehaviorHistory() {

         @Override
         public boolean isDone (CompoundBehavior behavior, DateTime since) {
            return behaviorHistory.isDone(
                  appendFocusRequestIfNecessary(behavior), since);
         }
      };
   }
}