package edu.wpi.disco.rt;

import edu.wpi.disco.rt.schema.Schema;
import edu.wpi.disco.rt.util.ThreadPools;
import java.util.concurrent.*;

public class Scheduler {

   public static int THREADS = Runtime.getRuntime().availableProcessors() * 2,
         DAEMON_THREADS = THREADS;
   
   private final ScheduledExecutorService executor, daemonExecutor;
   
   public Scheduler () { this(null, null); }
   
   public Scheduler (Class<? extends Throwable> handle, ExceptionHandler handler) {
      executor = ThreadPools.newScheduledThreadPool(THREADS, handle, handler, false);
      daemonExecutor = ThreadPools.newScheduledThreadPool(DAEMON_THREADS, handle, handler, true);
   }

   public ScheduledFuture<?> schedule (Runnable runnable, long interval, boolean daemon) {
      return (daemon? daemonExecutor : executor)
            .scheduleWithFixedDelay(runnable, 0, interval, TimeUnit.MILLISECONDS);
   }
   
   public interface ExceptionHandler {
      void handle (Runnable r, Throwable e);
   }
}
