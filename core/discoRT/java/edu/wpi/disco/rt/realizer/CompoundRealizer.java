package edu.wpi.disco.rt.realizer;

public interface CompoundRealizer extends Runnable {

   boolean isDone ();

   void addObserver (CompoundRealizerObserver observer);

   void removeObserver (CompoundRealizerObserver observer);
}
