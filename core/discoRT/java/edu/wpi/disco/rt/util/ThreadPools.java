package edu.wpi.disco.rt.util;

import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.cetask.Utils;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.schema.Schema;
import java.util.*;
import java.util.concurrent.*;

/**
 * Utility class with factory methods to make thread pools that use class names
 * to help debugging in Eclipse. Thread name shows current or last task executed
 * on that thread.
 * 
 * @see Executors
 */
public class ThreadPools {

   public static ExecutorService newFixedThreadPool (int nThreads) {
      return new ThreadPools.ThreadPoolExecutor(nThreads, nThreads, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
   }

   public static ExecutorService newCachedThreadPool () {
      return new ThreadPools.ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
   }

   public static ScheduledExecutorService newScheduledThreadPool (int corePoolSize) {
      
      return new ScheduledThreadPoolExecutor(corePoolSize) {

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
            try {
               super.afterExecute(r, t);
               ThreadPools.afterExecute(r, t);
            } finally { 
               if ( r instanceof ScheduledFutureTask<?> ) {
                  ScheduledFutureTask<?> task = (ScheduledFutureTask<?>) r;
                  if ( t != null || task.isDone() || task.isCancelled() ) {
                     r = task.getInner();
                     if ( r instanceof Schema ) ((Schema) r).dispose();
                  }
               }
            }
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
            long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
         super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
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
         ThreadPools.afterExecute(r, t);
      }
   }

   private static void afterExecute (Runnable r, Throwable t) {
      if ( t == null && Future.class.isAssignableFrom(r.getClass()) ) {
         if ( !((Future<?>) r).isDone() )
            return;
         try {
            ((Future<?>) r).get(1, TimeUnit.MILLISECONDS);
         } catch (CancellationException ce) {
            if ( DiscoRT.TRACE ) System.out.println("Cancelled " + getName(r));
         } catch (ExecutionException ee) {
            t = ee.getCause();
         } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
         } catch (TimeoutException te) {
            if ( DiscoRT.TRACE ) System.out.println("Timeout " + getName(r));
         }
      }
      if ( t != null ) { Utils.rethrow(t); }
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

   private static class ScheduledFutureTask<V> implements RunnableScheduledFuture<V> {

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
    
   /** Cannot instantiate. */
   private ThreadPools () {
   }
}
