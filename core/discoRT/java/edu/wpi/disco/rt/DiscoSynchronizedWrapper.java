package edu.wpi.disco.rt;

import edu.wpi.disco.*;
import edu.wpi.disco.rt.action.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Every call to Disco should be mediated by this class. It ensures synchronized
 * access to Disco
 * 
 * @author Bahador
 */
public class DiscoSynchronizedWrapper {

   private final Disco disco;
   private final ReentrantLock lock = new ReentrantLock();

   public DiscoSynchronizedWrapper (Disco disco) {
      this.disco = disco;
   }

   public void execute (DiscoAction task) {
      lock.lock();
      try {
         task.execute(disco);
      } finally {
         lock.unlock();
      }
   }

   public <T> T execute (DiscoFunc<T> func) {
      lock.lock();
      try {
         return func.execute(disco);
      } finally {
         lock.unlock();
      }
   }
}
