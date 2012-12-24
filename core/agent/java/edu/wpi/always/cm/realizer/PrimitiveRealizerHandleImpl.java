package edu.wpi.always.cm.realizer;

import edu.wpi.always.cm.utils.FutureValue;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class PrimitiveRealizerHandleImpl implements PrimitiveRealizerHandle,
      PrimitiveBehaviorControlObserver, PrimitiveRealizerObserver {

   private enum State {
      RunningDone, NotRunningDone, NotRunningNotDone
   }

   // if it is not set, it means it is Running but not Done
   FutureValue<State> state = new FutureValue<PrimitiveRealizerHandleImpl.State>();
   Lock stateWriteLock = new ReentrantLock();
   private PrimitiveBehavior behavior;

   public PrimitiveRealizerHandleImpl (PrimitiveBehavior behavior,
         PrimitiveRealizer<?> realizer, PrimitiveBehaviorControl control) {
      this.behavior = behavior;
      control.addObserver(this);
      realizer.addObserver(this);
   }

   @Override
   public boolean isDone () {
      State s = getStateIfSet();
      if ( s == null )
         return false;
      return s == State.NotRunningDone || s == State.RunningDone;
   }

   public State getStateIfSet () {
      if ( !state.isSet() )
         return null;
      try {
         return state.get();
      } catch (InterruptedException e) {
         e.printStackTrace();
         return null;
      } catch (ExecutionException e) {
         e.printStackTrace();
         return null;
      }
   }

   @Override
   public boolean isRunning () {
      State s = getStateIfSet();
      if ( s == null )
         return true;
      return s != State.NotRunningDone;
   }

   @Override
   public void waitUntilDoneOrCanceled () throws InterruptedException,
         ExecutionException {
      state.get();
   }

   @Override
   public void waitUntilDoneOrCanceled (long timeout)
         throws InterruptedException, ExecutionException, TimeoutException {
      state.get(timeout);
   }

   @Override
   public void primitiveDone (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
   }

   @Override
   public void primitiveStopped (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
      if ( pb.equals(behavior) ) {
         stateWriteLock.lock();
         try {
            if ( isDone() )
               state.set(State.NotRunningDone);
            else
               state.set(State.NotRunningNotDone);
         } finally {
            stateWriteLock.unlock();
         }
      }
   }

   @Override
   public PrimitiveBehavior getBehavior () {
      return behavior;
   }

   @Override
   public void prmitiveRealizerDone (PrimitiveRealizer<?> realizer) {
      stateWriteLock.lock();
      try {
         if ( isRunning() )
            state.set(State.RunningDone);
         else
            state.set(State.NotRunningDone);
      } finally {
         stateWriteLock.unlock();
      }
      realizer.removeObserver(this);
   }
}
