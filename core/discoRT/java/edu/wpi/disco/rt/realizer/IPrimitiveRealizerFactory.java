package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.behavior.PrimitiveBehavior;

public interface IPrimitiveRealizerFactory {

   PrimitiveRealizer<?> create (PrimitiveBehavior primitiveBehavior);
}
