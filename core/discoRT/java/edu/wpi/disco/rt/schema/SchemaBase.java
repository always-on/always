package edu.wpi.disco.rt.schema;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.util.concurrent.ScheduledFuture;

public abstract class SchemaBase implements Schema {

   public static int NORMAL_PRIORITY = 0;
   
   private final BehaviorProposalReceiver behaviorReceiver;
   protected final BehaviorHistory behaviorHistory;
   
   private TimeStampedValue<Behavior> lastProposal;
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
   public void stop () { 
      // mayInterruptIfRunning is false, because might leave 
      // something in inconsistent state
      if ( future != null ) future.cancel(false);
      future = null;
   }

   @Override
   public boolean isDone () {
      return future == null || future.isDone();
   }
   
   @Override
   public void dispose () {}
   
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
      updateLastProposal(behavior);
      behaviorReceiver.add(this, behavior, metadata);
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

}