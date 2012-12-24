package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.Resource;

public interface PrimitiveBehaviorControl {

   PrimitiveRealizerHandle realize (PrimitiveBehavior behavior);

   void addObserver (PrimitiveBehaviorControlObserver observer);

   void removeObserver (PrimitiveBehaviorControlObserver observer);

   void stop (Resource resource);
}
