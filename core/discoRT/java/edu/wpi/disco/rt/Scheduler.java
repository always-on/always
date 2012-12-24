package edu.wpi.disco.rt;

import java.util.concurrent.*;

public class Scheduler {

   private final ScheduledExecutorService executor = ThreadPools
         .newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);

   public void schedule (Runnable runnable, long interval) {
      executor.scheduleWithFixedDelay(runnable, 0, interval,
            TimeUnit.MILLISECONDS);
   }
}
