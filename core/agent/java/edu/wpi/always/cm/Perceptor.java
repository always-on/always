package edu.wpi.always.cm;

public interface Perceptor<T extends Perception> extends Runnable {

   public T getLatest ();
}
