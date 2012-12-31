package edu.wpi.disco.rt.realizer;

import java.util.concurrent.*;

public interface PrimitiveRealizerHandle {

   void waitUntilDoneOrCanceled (long timeout)
         throws InterruptedException, ExecutionException, TimeoutException;

   void waitUntilDoneOrCanceled () throws InterruptedException, ExecutionException;

   boolean isRunning ();

   boolean isDone ();

   PrimitiveBehavior getBehavior ();
};
