package edu.wpi.disco.rt.util;

import java.util.*;
import java.util.concurrent.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.Scheduler.ExceptionHandler;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.CompoundRealizer;
import edu.wpi.disco.rt.schema.Schema;

/**
 * Utility class with factory methods to make thread pools that use class names
 * to help debugging in Eclipse. Thread name shows current or last task executed
 * on that thread.
 * 
 * @see Executors
 */
public class ThreadPools {

   public static ExecutorService newFixedThreadPool (int nThreads, boolean daemon) {
      return new ThreadPools.ThreadPoolExecutor(nThreads, nThreads, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), daemon);
   }

   public static ExecutorService newCachedThreadPool (boolean daemon) {
      return new ThreadPools.ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), daemon);
   }

   public static ScheduledExecutorService newScheduledThreadPool (int corePoolSize, boolean daemon) {
      return newScheduledThreadPool(corePoolSize, null, null, daemon);
   }
   
   public static ScheduledExecutorService newScheduledThreadPool (int corePoolSize, 
         final Class<? extends Throwable> throwable, final ExceptionHandler handler, 
         boolean daemon) {
      
      return new ScheduledThreadPoolExecutor(corePoolSize, 
            daemon ? DAEMON_THREAD_FACTORY : Executors.defaultThreadFactory()) {

         @Override
         protected <V> RunnableScheduledFuture<V> decorateTask (
               Runnable runnable, RunnableScheduledFuture<V> task) {
            task = new ScheduledFutureTask<V>(task, runnable);
            names.put(task, runnable.getClass().getName());
            return task;
         }

         @Override
         protected void beforeExecute (Thread t, Runnable r) {
            t.setName(names.get(r));
            super.beforeExecute(t, r);
         }

         @Override
         protected void afterExecute (Runnable r, Throwable t) {
            super.afterExecute(r, t);
            ThreadPools.afterExecute(r, t, throwable, handler);
         }
      };
   }
   // note using weak hash map to avoid memory leak due to many tasks while
   // always on
   private static final Map<RunnableScheduledFuture<? extends Object>, String> names = Collections
         .synchronizedMap(new WeakHashMap<RunnableScheduledFuture<? extends Object>, String>());

   private static class ThreadPoolExecutor extends
         java.util.concurrent.ThreadPoolExecutor {

      private ThreadPoolExecutor (int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            boolean daemon) {
         super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
               daemon ? DAEMON_THREAD_FACTORY : Executors.defaultThreadFactory());
      }

      @Override
      protected <T> RunnableFuture<T> newTaskFor (Runnable runnable, T value) {
         return new ThreadPools.FutureTask<T>(runnable, value);
      }

      @Override
      protected void beforeExecute (Thread t, Runnable r) {
         t.setName(getName(r));
         super.beforeExecute(t, r);
      }

      @Override
      protected void afterExecute (Runnable r, Throwable t) {
         super.afterExecute(r, t);
         ThreadPools.afterExecute(r, t, null, null);
      }
   }

   private static void afterExecute (Runnable r, Throwable t, 
         Class<? extends Throwable> throwable, ExceptionHandler handler) {
      if ( t == null && r instanceof Future<?> ) {
         if ( !((Future<?>) r).isDone() ) return;
         try {
            ((Future<?>) r).get(1, TimeUnit.MILLISECONDS);
         } catch (CancellationException e) {
            if ( DiscoRT.TRACE ) Utils.lnprint(System.out, "Cancelled " + getName(r));
         } catch (ExecutionException e) {
            t = e.getCause();
            // run exception handler before dispose
            if ( t != null && throwable != null && throwable.isInstance(t) ) {
               handler.handle(r, t);
               t = null;
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // ignore/reset
         } catch (TimeoutException e) {
            if ( DiscoRT.TRACE ) Utils.lnprint(System.out, "Timeout " + getName(r));
         }
         if ( r instanceof ScheduledFutureTask<?> ) {
            ScheduledFutureTask<?> task = (ScheduledFutureTask<?>) r;
            r = task.getInner();
            if ( r instanceof Schema ) {
               Utils.lnprint(System.out, "Disposing of "+r+"...");
               ((Schema) r).dispose();
            }
         }
      }
      if ( t != null ) {
         if  ( throwable != null && throwable.isInstance(t) ) handler.handle(r, t);
         else {
            System.out.println(); // may improve readability
            edu.wpi.cetask.Utils.rethrow(t);
         }
      }
   }
   
   private static String getName (Runnable runnable) {
      if ( runnable instanceof ThreadPools.FutureTask<?> )
         runnable = ((ThreadPools.FutureTask<?>) runnable).getInner();
      if ( runnable instanceof Behavior || runnable instanceof CompoundBehavior
         || runnable instanceof CompoundRealizer )
         return runnable.toString();
      String name = names.get(runnable);
      if ( name != null ) return name;
      return runnable.getClass().getName();
   }

   private static class FutureTask<V> extends java.util.concurrent.FutureTask<V> {

      private final Runnable inner;

      private FutureTask (Runnable inner, V value) {
         super(inner, value);
         this.inner = inner;
      }

      public Runnable getInner () { return inner; }
   }

   public static class ScheduledFutureTask<V> implements RunnableScheduledFuture<V> {

      private final RunnableScheduledFuture<V> task;
      private final Runnable inner;

      private ScheduledFutureTask (RunnableScheduledFuture<V> task, Runnable inner) {
         this.task = task;
         this.inner = inner;
      }

      public Runnable getInner () { return inner; }

      @Override
      public long getDelay (TimeUnit arg0) { return task.getDelay(arg0); }

      @Override
      public int compareTo (Delayed arg0) { return task.compareTo(arg0); }

      @Override
      public boolean isPeriodic () { return task.isPeriodic(); }

      @Override
      public void run () { task.run(); }

      @Override
      public boolean cancel (boolean arg0) { return task.cancel(arg0); }

      @Override
      public V get () throws InterruptedException, ExecutionException {
         return task.get();
      }

      @Override
      public V get (long arg0, TimeUnit arg1) throws InterruptedException,
            ExecutionException, TimeoutException {
         return task.get(arg0, arg1);
      }

      @Override
      public boolean isCancelled () { return task.isCancelled(); }

      @Override
      public boolean isDone () { return task.isDone(); }
   }
    
   private static final ThreadFactory DAEMON_THREAD_FACTORY = new ThreadFactory () {
      
      private final ThreadFactory factory = Executors.defaultThreadFactory();
      
      @Override
      public Thread newThread (Runnable r) {
         Thread t = factory.newThread(r);
         t.setDaemon(true);
         return t;
      }};
      
   /** Cannot instantiate. */
   private ThreadPools () {
   }
}
