package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.*;

public interface Realizer {

	public abstract void realize(CompoundBehavior behavior);

	public abstract void freeUpResource(Resource r);

}