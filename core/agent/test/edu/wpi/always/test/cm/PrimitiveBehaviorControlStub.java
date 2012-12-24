package edu.wpi.always.test.cm;

import static com.google.common.collect.Lists.*;

import java.util.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

class PrimitiveBehaviorControlStub implements PrimitiveBehaviorControl {

	public PrimitiveBehaviorControlObserver observer;
	public List<PrimitiveBehavior> realizedBehaviors = newArrayList();

	@Override
	public PrimitiveRealizerHandle realize(PrimitiveBehavior behavior) {
		realizedBehaviors.add(behavior);
		return null;
	}

	@Override
	public void addObserver(PrimitiveBehaviorControlObserver observer) {
		this.observer = observer;
	}

	@Override
	public void removeObserver(PrimitiveBehaviorControlObserver observer) {
		this.observer = null;
	}

	@Override
	public void stop(Resource gaze) {
	}

}