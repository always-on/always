package edu.wpi.always.cm.realizer;

import java.util.concurrent.*;

public abstract class PrimitiveRealizerImplBase<T extends PrimitiveBehavior>
		implements PrimitiveRealizer<T> {

	private final T params;
	private final CopyOnWriteArrayList<PrimitiveRealizerObserver> observers = new CopyOnWriteArrayList<PrimitiveRealizerObserver>();

	public PrimitiveRealizerImplBase(T params) {
		this.params = params;
	}

	@Override
	public T getParams() {
		return params;
	}

	@Override
	public void shutdown() {
	}
	
	@Override
	public void addObserver(PrimitiveRealizerObserver observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(PrimitiveRealizerObserver observer) {
		observers.remove(observer);
	}

	protected void fireDoneMessage() {
		for(PrimitiveRealizerObserver o : observers) {
			o.prmitiveRealizerDone(this);
		}
	}
	
}
