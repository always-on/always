package edu.wpi.always.cm.realizer.petri;

import edu.wpi.always.cm.realizer.*;
import edu.wpi.always.cm.utils.*;

public class BehaviorStartPlace extends Place {

	private final FutureValue<PrimitiveRealizerHandle> handle = new FutureValue<PrimitiveRealizerHandle>();
	private final PrimitiveBehaviorControl control;
	private final PrimitiveBehavior behavior;
	
	public BehaviorStartPlace(PrimitiveBehavior behavior, PrimitiveBehaviorControl control) {
		this.behavior = behavior;
		this.control = control;
	}
	
	public FutureValue<PrimitiveRealizerHandle> GetRealizerHandle() {
		return handle;
	}

	@Override
	public void run() {
		System.out.println();
		System.out.println("starting on " + behavior.getClass());
		System.out.println();
		
		handle.set(control.realize(behavior));
		done();
	}
	
	@Override
	public String toString() {
		return behavior + ":" + "start";
	}
	
}
