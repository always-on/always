package edu.wpi.disco.rt.realizer.petri;

import edu.wpi.cetask.Utils;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.FutureValue;
import java.util.concurrent.ExecutionException;

public class BehaviorEndPlace extends Place {

   private final FutureValue<PrimitiveRealizerHandle> handleRef;
   private final PrimitiveBehavior behavior;

   public BehaviorEndPlace (FutureValue<PrimitiveRealizerHandle> handleRef,
         PrimitiveBehavior behavior) {
      this.handleRef = handleRef;
      this.behavior = behavior;
   }

   @Override
   public void run () {
      try {
         PrimitiveRealizerHandle h = handleRef.get();
         h.waitUntilDoneOrCanceled();
         if ( h.isDone() )
            done();
         else
            fail();
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      } catch (ExecutionException e) { Utils.rethrow(e); }
   }

   @Override
   public String toString () {
      return behavior + ":" + "end";
   }
}
