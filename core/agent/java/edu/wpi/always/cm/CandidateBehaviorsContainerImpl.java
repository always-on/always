package edu.wpi.always.cm;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import com.google.common.collect.*;

public class CandidateBehaviorsContainerImpl implements
		BehaviorProposalReceiver, CandidateBehaviorsContainer {
	private final Map<Schema, CandidateBehavior> candidates = Maps.newHashMap();
	private final ConcurrentLinkedQueue<CandidateBehavior> newPropsals = new ConcurrentLinkedQueue<CandidateBehavior>();
	private final ReentrantLock LOCK_candidates = new ReentrantLock();

	@Override
	public void add(Schema schema, Behavior behavior, BehaviorMetadata metadata) {
		newPropsals.add(new CandidateBehavior(behavior, schema, metadata));
	}

	@Override
	public List<CandidateBehavior> all() {
		copyNewProposalsToCandidatesList();

		return ImmutableList.copyOf(candidates.values());
	}

	// thread-safe, but not intended to be called from multiple threads
	// according to current design, only arbitrator would cause this to be
	// called
	private void copyNewProposalsToCandidatesList() {
		CandidateBehavior p = newPropsals.poll();
		while (p != null) {
			LOCK_candidates.lock();
			try {
				candidates.remove(p.getProposer());
				candidates.put(p.getProposer(), p);
			} finally {
				LOCK_candidates.unlock();
			}

			p = newPropsals.poll();
		}
	}

}
