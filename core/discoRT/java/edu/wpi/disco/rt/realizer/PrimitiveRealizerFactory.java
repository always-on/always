package edu.wpi.disco.rt.realizer;

public interface PrimitiveRealizerFactory {

   PrimitiveRealizer<?> create (PrimitiveBehavior primitiveBehavior);
}
