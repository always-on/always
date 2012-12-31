package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.Resource;

public interface IRealizer {

   public abstract void realize (CompoundBehavior behavior);

   public abstract void freeUpResource (Resource r);
}