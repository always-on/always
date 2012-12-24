package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.Resource;
import java.util.concurrent.*;

public class PrimitiveBehaviorControlDisconnectableWrapper implements
      PrimitiveBehaviorControl, PrimitiveBehaviorControlObserver {

   private final PrimitiveBehaviorControl inner;
   private final CopyOnWriteArrayList<PrimitiveBehaviorControlObserver> observers = new CopyOnWriteArrayList<PrimitiveBehaviorControlObserver>();
   private boolean disconnected;

   public PrimitiveBehaviorControlDisconnectableWrapper (
         PrimitiveBehaviorControl inner) {
      this.inner = inner;
      inner.addObserver(this);
   }

   @Override
   public PrimitiveRealizerHandle realize (PrimitiveBehavior behavior) {
      if ( isDisconnected() )
         return canceledRealizerHandle(behavior);
      return inner.realize(behavior);
   }

   private PrimitiveRealizerHandle canceledRealizerHandle (
         final PrimitiveBehavior behavior) {
      return new PrimitiveRealizerHandle() {

         @Override
         public void waitUntilDoneOrCanceled (long timeout)
               throws InterruptedException, ExecutionException,
               TimeoutException {
         }

         @Override
         public void waitUntilDoneOrCanceled () throws InterruptedException,
               ExecutionException {
         }

         @Override
         public boolean isRunning () {
            return false;
         }

         @Override
         public boolean isDone () {
            return false;
         }

         @Override
         public PrimitiveBehavior getBehavior () {
            return behavior;
         }
      };
   }

   @Override
   public void addObserver (PrimitiveBehaviorControlObserver observer) {
      observers.add(observer);
   }

   @Override
   public void removeObserver (PrimitiveBehaviorControlObserver observer) {
      observers.remove(observer);
   }

   @Override
   public void stop (Resource resource) {
      if ( isDisconnected() )
         return;
      inner.stop(resource);
   }

   @Override
   public void primitiveDone (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
      if ( isDisconnected() )
         return;
      for (PrimitiveBehaviorControlObserver o : observers) {
         o.primitiveDone(this, pb);
      }
   }

   public void disconnect () {
      inner.removeObserver(this);
      disconnected = true;
   }

   public boolean isDisconnected () {
      return disconnected;
   }

   @Override
   public void primitiveStopped (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
      if ( isDisconnected() )
         return;
      for (PrimitiveBehaviorControlObserver o : observers) {
         o.primitiveStopped(this, pb);
      }
   }
}
