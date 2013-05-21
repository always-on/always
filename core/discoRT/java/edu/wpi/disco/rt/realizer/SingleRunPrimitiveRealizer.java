package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import java.util.concurrent.locks.ReentrantLock;

public abstract class SingleRunPrimitiveRealizer<T extends PrimitiveBehavior>
      extends PrimitiveRealizerBase<T> {

   Boolean ranOnce = false, done = false;
   ReentrantLock lock = new ReentrantLock();

   public SingleRunPrimitiveRealizer (T params) {
      super(params);
   }

   @Override
   public void run () {
      if ( ranOnce ) {
         if ( done )
            fireDoneMessage();
         return;
      }
      lock.lock();
      try {
         if ( !ranOnce ) {
            ranOnce = true;
            singleRun();
         }
      } finally {
         lock.unlock();
      }
   }

   @Override
   protected void fireDoneMessage () {
      super.fireDoneMessage();
      done = true;
   }

   protected abstract void singleRun ();
}
