package edu.wpi.always.cm.realizer.petri;

import edu.wpi.always.cm.realizer.*;
import edu.wpi.always.cm.utils.FutureValue;
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
      } catch (ExecutionException e) {
         e.printStackTrace();
         fail();
      }
   }

   @Override
   public String toString () {
      return behavior + ":" + "end";
   }
}
