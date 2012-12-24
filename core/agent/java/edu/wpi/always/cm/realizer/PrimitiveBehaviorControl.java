package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.Resource;

public interface PrimitiveBehaviorControl {

   PrimitiveRealizerHandle realize (PrimitiveBehavior behavior);

   void addObserver (PrimitiveBehaviorControlObserver observer);

   void removeObserver (PrimitiveBehaviorControlObserver observer);

   void stop (Resource resource);
}
