package edu.wpi.always.cm.realizer;

public interface PrimitiveRealizerFactory {

   PrimitiveRealizer<?> create (PrimitiveBehavior primitiveBehavior);
}
