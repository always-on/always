package edu.wpi.disco.rt.realizer.petri;

import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.FutureValue;

public class BehaviorStartPlace extends Place {

   private final FutureValue<PrimitiveRealizerHandle> handle = new FutureValue<PrimitiveRealizerHandle>();
   private final PrimitiveBehaviorControl control;
   private final PrimitiveBehavior behavior;

   public BehaviorStartPlace (PrimitiveBehavior behavior,
         PrimitiveBehaviorControl control) {
      this.behavior = behavior;
      this.control = control;
   }

   public FutureValue<PrimitiveRealizerHandle> getRealizerHandle () {
      return handle;
   }

   @Override
   public void run () {
      // System.out.println("starting on " + behavior.getClass());
      handle.set(control.realize(behavior));
      done();
   }

   @Override
   public String toString () {
      return behavior + ":" + "start";
   }
}
