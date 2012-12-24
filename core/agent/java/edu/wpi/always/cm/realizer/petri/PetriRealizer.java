package edu.wpi.always.cm.realizer.petri;

import com.google.common.collect.Maps;

import edu.wpi.always.cm.realizer.*;
import edu.wpi.always.cm.utils.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PetriRealizer implements CompoundRealizer {
	private final CompoundBehaviorWithConstraints behavior;
	private final Map<PrimitiveBehavior, BehaviorStartPlace> startPlaces = Maps.newHashMap();
	private final Map<PrimitiveBehavior, BehaviorEndPlace> endPlaces = Maps.newHashMap();
	private final PrimitiveBehaviorControl control;
	private final List<CompoundRealizerObserver> observers = new CopyOnWriteArrayList<CompoundRealizerObserver>();
	private boolean done;

	public PetriRealizer(CompoundBehaviorWithConstraints behavior, PrimitiveBehaviorControl control) {
		this.behavior = behavior;
		this.control = control;
	}

	public CompoundBehaviorWithConstraints getBehavior() {
		return behavior;
	}

	@Override
	public void run() {
		Transition start = createBasicStructure();
		createConstraints();

		PetriNetRunner petriRunner = new PetriNetRunner(start);
		petriRunner.run();

		if (!petriRunner.getFailedPlaces().isEmpty()) {
			System.out.println();
			System.out.println("WARNING: PetriRealizer failed in " + petriRunner.getFailedPlaces().size() + " places!");
			System.out.println();
		}

		setDone();
	}

	private void setDone() {
		done = true;

		for (CompoundRealizerObserver o : observers)
			o.compoundRealizerDone(this);
	}

	// This function only creates the structure for primitives, without
	// considering the constraints
	private Transition createBasicStructure() {
		Transition firstTransition = new Transition();
		Place firstPlace = new Place();
		firstTransition.addOutput(firstPlace);

		for (PrimitiveBehavior pb : behavior.getPrimitives()) {
			Place prevPlace = firstPlace;
			for (SyncPoint syncPoint : SyncPoint.values()) {
				Place curPlace = getPlace(pb, syncPoint);

				Transition before = new Transition();

				curPlace.setInput(before);
				before.addInput(prevPlace);

				prevPlace = curPlace;
			}
		}

		return firstTransition;
	}

	private void createConstraints() {
		for (Constraint c : behavior.getConstraints()) {
			switch (c.getType()) {
			case Sync:
				synchronize(c.getFirst(), c.getSecond());
				break;
			case After:
				after(c.getFirst(), c.getSecond(), c.getOffset());
				break;
			case Before:
				before(c.getFirst(), c.getSecond(), c.getOffset());
				break;
			}
		}
	}

	private void after(SyncRef first, SyncRef second, int offset) {
		if (offset >= 0) {
			Place p1 = getPlace(first);
			Place p2 = getPlace(second);

			if(offset == 0) {
				p1.addOutput(p2.getInput());
			} else {
				TimerPlace tp = new TimerPlace(offset);
				Transition beforetp = new Transition();
				beforetp.addOutput(tp);

				p1.addOutput(beforetp);
				
				tp.addOutput(p2.getInput());
			}
		} else {
			before(first, second, -offset);
		}
	}

	private void before(SyncRef first, SyncRef second, int offset) {
		after(second, first, offset);
	}

	private void synchronize(SyncRef first, SyncRef second) {
		Place p1 = getPlace(first);
		Place p2 = getPlace(second);

		p1.getInput().mergeWith(p2.getInput());
	}

	private Place getPlace(SyncRef ref) {
		return getPlace(ref.getBehavior(), ref.getSyncPoint());
	}

	private Place getPlace(PrimitiveBehavior pb, SyncPoint syncPoint) {
		if (syncPoint == null)
			throw new RArgumentNullException("syncPoint");

		if (pb == null)
			throw new RArgumentNullException("pb");

		switch (syncPoint) {
		case Start:
			if (startPlaces.containsKey(pb))
				return startPlaces.get(pb);

			return createStartPlace(pb);
		case End:
			if (endPlaces.containsKey(pb))
				return endPlaces.get(pb);

			return createEndPlace(pb);
		}

		throw new RuntimeException("SyncPoint not supported: " + syncPoint);
	}

	private Place createEndPlace(PrimitiveBehavior pb) {
		BehaviorEndPlace p = new BehaviorEndPlace(getRealizerHandleFor(pb), pb);
		endPlaces.put(pb, p);
		return p;
	}

	private Place createStartPlace(PrimitiveBehavior pb) {
		BehaviorStartPlace p = new BehaviorStartPlace(pb, control);
		startPlaces.put(pb, p);
		return p;
	}

	private FutureValue<PrimitiveRealizerHandle> getRealizerHandleFor(PrimitiveBehavior pb) {
		BehaviorStartPlace p = (BehaviorStartPlace) getPlace(pb, SyncPoint.Start);
		return p.GetRealizerHandle();
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public void addObserver(CompoundRealizerObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(CompoundRealizerObserver observer) {
		observers.remove(observer);
	}
	
	@Override
	public String toString () { return behavior.toString();	}
}

