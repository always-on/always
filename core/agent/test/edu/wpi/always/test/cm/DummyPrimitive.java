package edu.wpi.always.test.cm;

import edu.wpi.always.cm.*;
import edu.wpi.disco.rt.realizer.*;

class DummyPrimitive extends PrimitiveBehavior {

	Integer fakeParam;
	private final Resource resource;

	public DummyPrimitive(Resource resource,
			int somethingToDistinguishItFromOthers) {
		this.resource = resource;
		this.fakeParam = somethingToDistinguishItFromOthers;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof DummyPrimitive))
			return false;

		DummyPrimitive theOther = (DummyPrimitive) o;

		if (theOther.resource != this.resource)
			return false;

		return theOther.fakeParam == this.fakeParam;
	}

	@Override
	public int hashCode() {
		return fakeParam.hashCode();
	}

	@Override
	public String toString() {
		return "DummyPrimitive(" + fakeParam + ")";
	}

}