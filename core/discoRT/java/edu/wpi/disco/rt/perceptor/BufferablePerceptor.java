package edu.wpi.disco.rt.perceptor;

public interface BufferablePerceptor<T extends Perception> extends Perceptor<T> {

   public PerceptorBuffer<T> newBuffer ();
}
