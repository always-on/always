package edu.wpi.always.cm.realizer;

import java.util.concurrent.locks.ReentrantLock;

public abstract class SingleRunPrimitiveRealizer<T extends PrimitiveBehavior>
      extends PrimitiveRealizerImplBase<T> {

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
