package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.Resource;

public interface Realizer {

   public abstract void realize (CompoundBehavior behavior);

   public abstract void freeUpResource (Resource r);
}