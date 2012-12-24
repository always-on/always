package edu.wpi.disco.rt.realizer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class CompoundRealizerImplBase implements CompoundRealizer {

   protected final List<CompoundRealizerObserver> observers = new CopyOnWriteArrayList<CompoundRealizerObserver>();

   protected void notifyDone () {
      for (CompoundRealizerObserver o : observers) {
         o.compoundRealizerDone(this);
      }
   }

   @Override
   public void addObserver (CompoundRealizerObserver observer) {
      observers.add(observer);
   }

   @Override
   public void removeObserver (CompoundRealizerObserver observer) {
      observers.remove(observer);
   }
}
