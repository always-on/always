package edu.wpi.disco.rt.test;

import java.util.concurrent.*;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.SchemaBase;

public class DummySchema extends SchemaBase {

   @SuppressWarnings("rawtypes")
   public DummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor) {
      super(behaviorReceiver, resourceMonitor);
      setFuture(new ScheduledFuture() {

         @Override
         public long getDelay (TimeUnit arg0) {
            return 0;
         }
         
         @Override
         public int compareTo (Delayed arg0) {
            return 0;
         }

         @Override
         public boolean cancel (boolean mayInterruptIfRunning) {
            return false;
         }

         @Override
         public Object get () throws InterruptedException, ExecutionException {
            return null;
         }

         @Override
         public Object get (long timeout, TimeUnit unit)
               throws InterruptedException, ExecutionException,
               TimeoutException {
            return null;
         }

         @Override
         public boolean isCancelled () {
            return false;
         }

         @Override
         public boolean isDone () {
            return false;
         }});
   }

   @Override
   public void run () {
   }
}
