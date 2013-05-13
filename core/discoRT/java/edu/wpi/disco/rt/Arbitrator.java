package edu.wpi.disco.rt;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.Schema;
import java.util.List;

public class Arbitrator implements Runnable {

   private final ICandidateBehaviorsContainer candidateBehaviors;
   private final IRealizer realizer;
   private final ArbitrationStrategy strategy;
   private Schema focus;

   public Arbitrator (ArbitrationStrategy strategy, IRealizer realizer,
         ICandidateBehaviorsContainer behaviors) {
      this.strategy = strategy;
      this.realizer = realizer;
      candidateBehaviors = behaviors;
   }

   @Override
   public void run () {
      List<Resource> freeResources = Lists.newArrayList(Resources.values());
      List<CandidateBehavior> proposals = filterOutEmptyCandidates(candidateBehaviors
            .all());
      List<CandidateBehavior> selected = Lists.newArrayList();
      while (!freeResources.isEmpty() && !proposals.isEmpty()) {
         CandidateBehavior f = null;
         if ( freeResources.contains(Resources.FOCUS) )
            f = findFocusOfConversation(proposals);
         CandidateBehavior a = decide(proposals, f);
         if ( a.getBehavior().getResources().contains(Resources.FOCUS) )
            setCurrentFocus(a.getProposer());
         freeResources.removeAll(a.getBehavior().getResources());
         selected.add(a);
         for (CandidateBehavior p : Lists.newArrayList(proposals)) {
            for (Resource r : p.getBehavior().getResources()) {
               if ( !freeResources.contains(r) ) {
                  proposals.remove(p);
                  break;
               }
            }
         }
      }
      for (CandidateBehavior p : selected)
         execute(p.getBehavior().getInner());
      for (Resource r : freeResources) {
         realizer.freeUpResource(r);
      }
   }

   private List<CandidateBehavior> filterOutEmptyCandidates (
         List<CandidateBehavior> source) {
      List<CandidateBehavior> result = Lists.newArrayList();
      for (CandidateBehavior c : source)
         if ( !c.getBehavior().isEmpty() )
            result.add(c);
      return result;
   }

   private void setCurrentFocus (Schema proposer) {
      focus = proposer;
      proposer.focus();
      System.out.println("SetCurrentFocus: "+proposer);
   }

   /**
    * NOT thread-safe
    */
   private void execute (CompoundBehavior behavior) {
      realize(behavior);
   }

   public void realize (CompoundBehavior behavior) {
      realizer.realize(behavior);
   }

   private CandidateBehavior findFocusOfConversation (
         List<CandidateBehavior> candidates) {
      CandidateBehavior focusedOne = null;
      for (CandidateBehavior c : candidates) {
         if ( c.getProposer() == focus ) {
            if ( !c.getBehavior().isEmpty() )
               focusedOne = c;
            break;
         }
      }
      return focusedOne;
   }

   private CandidateBehavior decide (List<CandidateBehavior> candidates,
         CandidateBehavior focusedOne) {
      if ( focusedOne == null )
         return strategy.decide(candidates);
      return strategy.decide(candidates, focusedOne);
   }
}
