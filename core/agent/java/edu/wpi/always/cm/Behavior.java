package edu.wpi.always.cm;

import java.util.*;

import edu.wpi.always.cm.realizer.*;

public class Behavior {
	public static Behavior NULL = new Behavior(
			Collections.<PrimitiveBehavior> emptyList());

	private final CompoundBehavior inner;

	private Behavior(List<PrimitiveBehavior> required) {
		this.inner = new SimpleCompoundBehavior(required);
	}

	public Behavior(CompoundBehavior behavior) {
		this.inner = behavior;
	}

	public static Behavior newInstance(List<PrimitiveBehavior> required) {
		return new Behavior(required);
	}

	public static Behavior newInstance(PrimitiveBehavior required) {
		return newInstance(Collections.singletonList(required));
	}

	public static Behavior newInstance(PrimitiveBehavior... required) {
		return newInstance(Arrays.asList(required));
	}

	public CompoundBehavior getInner() {
		return inner;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof Behavior))
			return false;

		Behavior theOther = (Behavior) o;

		return this.inner.equals(theOther.inner);
	}

	public boolean isEmpty() {
		return getInner().getResources().isEmpty();
	}
	
	@Override
	public String toString () { return getInner().toString(); }
		
	public Set<Resource> getResources() {
		return inner.getResources();
	}
	
}
