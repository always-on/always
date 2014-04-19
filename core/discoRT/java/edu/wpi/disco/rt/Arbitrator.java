package edu.wpi.disco.rt;

import com.google.common.collect.Lists;
import edu.wpi.cetask.*;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.IRealizer;
import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.Utils;
import java.util.*;

public class Arbitrator implements Runnable {

   private final ICandidateBehaviorsContainer candidateBehaviors;
   private final IRealizer realizer;
   private final ArbitrationStrategy strategy;
   private final Interaction interaction;
   private final DiscoRT discoRT;
   private Schema focusSchema;
   private final List<Resource> freeResources = new ArrayList<Resource>();
   private final List<CandidateBehavior> proposals = new ArrayList<CandidateBehavior>(),
         selected = new ArrayList<CandidateBehavior>();

   public Schema getFocus () { return focusSchema; }
   
   public Arbitrator (ArbitrationStrategy strategy, IRealizer realizer,
         ICandidateBehaviorsContainer behaviors, DiscoRT discoRT) {
      this.strategy = strategy;
      this.realizer = realizer;
      candidateBehaviors = behaviors;
      this.discoRT = discoRT;
      this.interaction = discoRT.getInteraction();
   }

   private final static List<Resource> RESOURCES = Arrays.asList(Resources.values());
   
   @Override
   public void run () {
      // recoded to reduce gc for long running
      freeResources.clear();
      freeResources.addAll(RESOURCES);
      proposals.clear();
      proposals.addAll(candidateBehaviors.all());
      selected.clear();
      // first assign discourse focus based on Disco 
      // unless there is a higher priority proposal
      Plan focusPlan = interaction.getFocusExhausted(true);
      Class<? extends Schema> schema = 
            discoRT.getSchema(focusPlan == null ? null :
               focusPlan.getType());
      CandidateBehavior focusProposal = null;
      if ( schema != null ) {
         for (CandidateBehavior proposal : proposals) {
            Schema proposer = proposal.getProposer();
            if ( schema.isAssignableFrom(proposer.getClass()) ) {
               if ( proposal.getBehavior().getResources().contains(Resources.FOCUS) 
                     && !hasMoreSpecific(proposals, proposal) ) {
                  choose(proposal);
                  focusProposal = proposal;
                  proposer.focus();
                  if ( proposer != focusSchema ) {
                     focusSchema = proposer;
                     /* if ( DiscoRT.TRACE ) */ Utils.lnprint(System.out, "New focus: "+focusSchema);
                  }
                  break;
               }
            }
         }
         if ( focusProposal == null ) focusSchema = null;
         else focusProposal = null; // for decide
      } else { // Disco doesn't care, so let current focus schema keep going
         for (CandidateBehavior proposal : proposals)
            if ( proposal.getProposer() == focusSchema && !proposal.getBehavior().isEmpty() ) {
               focusProposal = proposal; 
               break;
            }
      }
      // wait for focus schema to make proposal if needed
      freeResources.remove(Resources.FOCUS);
      remove();
      while ( !freeResources.isEmpty() && !proposals.isEmpty() )
         choose(decide(proposals, focusProposal));
      for (CandidateBehavior p : selected) execute(p.getBehavior().getInner());
      for (Resource r : freeResources) realizer.freeUpResource(r);
   }

   private void choose (CandidateBehavior chosen) {
      freeResources.removeAll(chosen.getBehavior().getResources());
      remove();
      selected.add(chosen);
   }
   
   private void remove () { // proposals with no resources available
      Iterator<CandidateBehavior> iterator = proposals.iterator();
      while (iterator.hasNext())
         for (Resource r : iterator.next().getBehavior().getResources()) {
            if ( !freeResources.contains(r) ) {
               iterator.remove();
               break;
            }
      }
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

   private CandidateBehavior decide (List<CandidateBehavior> candidates,
         CandidateBehavior focusedOne) {
      return ( focusedOne == null || hasMoreSpecific(candidates, focusedOne) )? 
          strategy.decide(candidates) :
          strategy.decide(candidates, focusedOne);
   }
   
   private static boolean hasMoreSpecific (List<CandidateBehavior> candidates, CandidateBehavior candidate) {
      double specificity = candidate.getMetadata().getSpecificity();
      for (CandidateBehavior proposal : candidates) 
         if ( proposal.getMetadata().getSpecificity() > specificity)
            return true;
      return false;
   }
}
