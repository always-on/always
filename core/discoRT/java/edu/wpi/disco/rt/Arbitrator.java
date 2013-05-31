package edu.wpi.disco.rt;

import com.google.common.collect.Lists;
import edu.wpi.cetask.*;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.rt.behavior.CompoundBehavior;
import edu.wpi.disco.rt.realizer.IRealizer;
import edu.wpi.disco.rt.schema.Schema;
import java.util.List;

public class Arbitrator implements Runnable {

   private final ICandidateBehaviorsContainer candidateBehaviors;
   private final IRealizer realizer;
   private final ArbitrationStrategy strategy;
   private final Interaction interaction;
   private final DiscoRT discoRT;
   private Schema focusSchema;
   private List<Resource> freeResources;
   private List<CandidateBehavior> proposals, selected;

   public Arbitrator (ArbitrationStrategy strategy, IRealizer realizer,
         ICandidateBehaviorsContainer behaviors, DiscoRT discoRT) {
      this.strategy = strategy;
      this.realizer = realizer;
      candidateBehaviors = behaviors;
      this.discoRT = discoRT;
      this.interaction = discoRT.getInteraction();
   }

   @Override
   public void run () {
      freeResources = Lists.newArrayList(Resources.values());
      proposals = filterOutEmptyCandidates(candidateBehaviors.all());
      selected = Lists.newArrayList();
      // first assign focus based on Disco
      Plan focusPlan = interaction.getFocusExhausted(true);
      Class<? extends Schema> schema = focusPlan == null ?
            discoRT.getSchema(null) : getSchema(focusPlan);
      CandidateBehavior focusProposal = null;
      if ( schema != null ) {
         for (CandidateBehavior proposal : proposals) {
            Schema proposer = proposal.getProposer();
            if ( schema.isAssignableFrom(proposer.getClass()) ) {
               if ( proposal.getBehavior().getResources().contains(Resources.FOCUS) ) {
                  choose(proposal);
                  focusProposal = proposal;
                  proposer.focus();
                  if ( proposer != focusSchema ) {
                     focusSchema = proposer;
                     /* if ( DiscoRT.TRACE ) */ System.out.println("New focus: "+focusSchema);
                  }
                  break;
               }
            }
         }
         if ( focusProposal == null ) focusSchema = null;
         else focusProposal = null; // for decide
      } else { // Disco doesn't care
         for (CandidateBehavior proposal : proposals)
            if ( proposal.getProposer() == focusSchema && !proposal.getBehavior().isEmpty() ) { 
               focusProposal = proposal; 
               break;
            }
      }
      while ( !freeResources.isEmpty() && !proposals.isEmpty() )
         choose(decide(proposals, focusProposal));
      for (CandidateBehavior p : selected) execute(p.getBehavior().getInner());
      for (Resource r : freeResources) realizer.freeUpResource(r);
   }

   private Class<? extends Schema> getSchema (Plan plan) {
      Class<? extends Schema> schema = discoRT.getSchema(plan.getType());
      return schema != null ? schema :
         interaction.getDisco().isTop(plan) ? null : getSchema(plan.getParent());
   }

   private void choose (CandidateBehavior chosen) {
      freeResources.removeAll(chosen.getBehavior().getResources());
      selected.add(chosen);
      for (CandidateBehavior p : Lists.newArrayList(proposals)) {
         for (Resource r : p.getBehavior().getResources()) {
            if ( !freeResources.contains(r) ) {
               proposals.remove(p);
               break;
            }
         }
      }
   }
   
   private List<CandidateBehavior> filterOutEmptyCandidates (List<CandidateBehavior> source) {
      List<CandidateBehavior> result = Lists.newArrayList();
      for (CandidateBehavior c : source)
         if ( !c.getBehavior().isEmpty() )
            result.add(c);
      return result;
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
      return focusedOne == null ? strategy.decide(candidates) :
         strategy.decide(candidates, focusedOne);
   }
}
