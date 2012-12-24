package edu.wpi.always.cm.realizer;

public interface CompoundRealizer extends Runnable {

   boolean isDone ();

   void addObserver (CompoundRealizerObserver observer);

   void removeObserver (CompoundRealizerObserver observer);
}
