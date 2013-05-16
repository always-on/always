package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.CompoundBehavior;

public interface IRealizer {

   public abstract void realize (CompoundBehavior behavior);

   public abstract void freeUpResource (Resource r);
}