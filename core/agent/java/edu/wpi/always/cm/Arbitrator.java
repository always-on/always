package edu.wpi.always.cm;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.realizer.*;

public class Arbitrator implements Runnable {

	private final CandidateBehaviorsContainer candidateBehaviors;
	private final Realizer realizer;
	private Schema focus;
	private ArbitrationStrategy strategy;

	public Arbitrator (ArbitrationStrategy strategy, Realizer realizer, CandidateBehaviorsContainer behaviors) {
		this.strategy = strategy;
		this.realizer = realizer;
		candidateBehaviors = behaviors;
	}

	public void run () {
		List<Resource> freeResources = Lists.newArrayList(Resource.values());
		List<CandidateBehavior> proposals = filterOutEmptyCandidates(candidateBehaviors.all());
		List<CandidateBehavior> selected = Lists.newArrayList();
		
		while(!freeResources.isEmpty() && !proposals.isEmpty()) {
			CandidateBehavior f = null;
		System.out.println("DECIDE "+selected);///////////////////////////////////

			if(freeResources.contains(Resource.Focus))
				f = findFocusOfConversation(proposals); // 
			
			CandidateBehavior a = decide(proposals, f);
	
			if (a.getBehavior().getResources().contains(Resource.Focus))
				setCurrentFocus(a.getProposer());
			
			freeResources.removeAll(a.getBehavior().getResources());
			
			selected.add(a);
			
			for(CandidateBehavior p : Lists.newArrayList(proposals)) {
				for(Resource r : p.getBehavior().getResources()) {
					if(!freeResources.contains(r)) {//yourtvseri.es
						proposals.remove(p);
						break;
					}
				}
			}
		}
		
		for(CandidateBehavior p : selected)
			execute(p.getBehavior().getInner());

		for (Resource r : freeResources) {
			realizer.freeUpResource(r);
		}
}

	private List<CandidateBehavior> filterOutEmptyCandidates(List<CandidateBehavior> source) {
		List<CandidateBehavior> result = Lists.newArrayList();
		
		for(CandidateBehavior c : source)
			if(!c.getBehavior().isEmpty())
				result.add(c);
		
		return result;
	}

	private void setCurrentFocus(Schema proposer) {
		focus = proposer;
	}

	/**
	 * NOT thread-safe
	 * 
	 * @param behavior
	 */
	private void execute (CompoundBehavior behavior) {
		realize(behavior);
	}

	public void realize(CompoundBehavior behavior) {
		realizer.realize(behavior);
	}
	
	private CandidateBehavior findFocusOfConversation(List<CandidateBehavior> candidates) {
		CandidateBehavior focusedOne = null;

		for (CandidateBehavior c : candidates) {
			if (c.getProposer() == focus) {
				if (!c.getBehavior().isEmpty())
					focusedOne = c;

				break;
			}
		}
		return focusedOne;
	}

	private CandidateBehavior decide(List<CandidateBehavior> candidates,
			CandidateBehavior focusedOne) {
		if(focusedOne == null)
			return strategy.decide(candidates);
		
		return strategy.decide(candidates, focusedOne);
	}

}
