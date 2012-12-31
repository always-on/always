package edu.wpi.disco.rt.realizer;

public interface IPrimitiveRealizerFactory {

   PrimitiveRealizer<?> create (PrimitiveBehavior primitiveBehavior);
}
