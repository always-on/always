package edu.wpi.disco.rt;

import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.ThreadPools;
import java.util.concurrent.*;

public class Scheduler {

   private final ScheduledExecutorService executor;
   
   public Scheduler () { this(null, null); }
   
   public Scheduler (Class<? extends Throwable> handle, ExceptionHandler handler) {
      executor = ThreadPools.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2, 
                             handle, handler);
   }

   public ScheduledFuture<?> schedule (Runnable runnable, long interval) {
      return executor.scheduleWithFixedDelay(runnable, 0, interval,
            TimeUnit.MILLISECONDS);
   }
   
   public interface ExceptionHandler {
      void handle (Runnable r, Throwable e);
   }
}
