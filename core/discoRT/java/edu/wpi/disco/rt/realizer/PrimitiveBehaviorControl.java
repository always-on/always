package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;

public interface PrimitiveBehaviorControl {

   PrimitiveRealizerHandle realize (PrimitiveBehavior behavior);

   void addObserver (PrimitiveBehaviorControlObserver observer);

   void removeObserver (PrimitiveBehaviorControlObserver observer);

   void stop (Resource resource);
}
