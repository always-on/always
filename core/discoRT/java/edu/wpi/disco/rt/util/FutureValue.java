package edu.wpi.disco.rt.util;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class FutureValue<T> {

   private volatile T val;
   private Lock valueLock = new ReentrantLock();
   private Condition condition = valueLock.newCondition();

   /**
    * if the value is not set yet, it will block the thread and wait for it
    * indefinitely
    * 
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public T get () throws InterruptedException, ExecutionException {
      waitToBeSet();
      return val;
   }

   private void waitToBeSet () throws InterruptedException {
      waitToBeSet(0);
   }

   /**
    * @param timeout in milliseconds
    * @throws InterruptedException
    */
   private void waitToBeSet (long timeout) throws InterruptedException {
      while (!isSet()) {
         double startTime = System.nanoTime();
         valueLock.lock();
         try {
            if ( !isSet() ) { // double check after getting the lock
               if ( timeout > 0 )
                  condition.await(timeout, TimeUnit.MILLISECONDS);
               else
                  condition.await();
            } else {
               break;
            }
         } finally {
            valueLock.unlock();
         }
         if ( timeout > 0 ) {
            double elapsed = (System.nanoTime() - startTime) * 0.000001;
            if ( elapsed >= timeout - 1 ) {
               break;
            } else {
               timeout = Math.round(timeout - elapsed);
               if ( timeout < 1 )
                  timeout = 1;
            }
         }
      }
   }

   /**
    * if the value is not set yet, waits a maximum of timeout milliseconds for
    * it (blocking the thread)
    * 
    * @param timeout in milliseconds
    * @return
    * @throws InterruptedException
    * @throws ExecutionException
    * @throws TimeoutException
    */
   public T get (long timeout) throws InterruptedException, ExecutionException,
         TimeoutException {
      waitToBeSet(timeout);
      return val;
   }

   public boolean isSet () {
      return val != null;
   }

   public void set (T value) {
      if ( value == null )
         throw new RuntimeException("trying to set value to null");
      valueLock.lock();
      try {
         this.val = value;
         condition.signalAll();
      } finally {
         valueLock.unlock();
      }
   }
}
