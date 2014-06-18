package edu.wpi.disco.rt;

import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.ThreadPools;
import java.util.concurrent.*;

public class Scheduler {

   private final ScheduledExecutorService executor, daemonExecutor;
   
   public Scheduler () { this(null, null); }
   
   public Scheduler (Class<? extends Throwable> handle, ExceptionHandler handler) {
      int size = Runtime.getRuntime().availableProcessors() * 2;
      executor = ThreadPools.newScheduledThreadPool(size, handle, handler, false);
      daemonExecutor = ThreadPools.newScheduledThreadPool(size, handle, handler, true);
   }

   public ScheduledFuture<?> schedule (Runnable runnable, long interval, boolean daemon) {
      return (daemon? daemonExecutor : executor)
            .scheduleWithFixedDelay(runnable, 0, interval, TimeUnit.MILLISECONDS);
   }
   
   public interface ExceptionHandler {
      void handle (Runnable r, Throwable e);
   }
}
