package edu.wpi.disco.rt.perceptor;

public interface Perceptor<T extends Perception> extends Runnable {

   public T getLatest ();
}
