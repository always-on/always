package edu.wpi.disco.rt;

import com.google.common.collect.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.Schema;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class CandidateBehaviorsContainer implements
      BehaviorProposalReceiver, ICandidateBehaviorsContainer {

   private final Map<Schema, CandidateBehavior> candidates = Maps.newHashMap();
   private final ConcurrentLinkedQueue<CandidateBehavior> newProposals = new ConcurrentLinkedQueue<CandidateBehavior>();
   private final ReentrantLock LOCK_candidates = new ReentrantLock();

   @Override
   public void add (Schema schema, Behavior behavior, BehaviorMetadata metadata) {
      newProposals.add(new CandidateBehavior(behavior, schema, metadata));
   }
   
   @Override
   public List<CandidateBehavior> all () {
      copyNewProposalsToCandidatesList();
      Iterator<Map.Entry<Schema,CandidateBehavior>> i = candidates.entrySet().iterator();
      while (i.hasNext())
         if ( i.next().getKey().isDone() ) i.remove();
      return ImmutableList.copyOf(candidates.values());
   }

   // thread-safe, but not intended to be called from multiple threads
   // according to current design, only arbitrator would cause this to be
   // called
   private void copyNewProposalsToCandidatesList () {
      CandidateBehavior p = newProposals.poll();
      while (p != null) {
         LOCK_candidates.lock();
         try {
            candidates.remove(p.getProposer());
            candidates.put(p.getProposer(), p);
         } finally {
            LOCK_candidates.unlock();
         }
         p = newProposals.poll();
      }
   }
}
