package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.Resource;

public interface Realizer {

   public abstract void realize (CompoundBehavior behavior);

   public abstract void freeUpResource (Resource r);
}