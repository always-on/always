package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.IdleBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class IdleBehaviorRealizer extends
      SingleRunPrimitiveRealizer<IdleBehavior> {

   private final ClientProxy proxy;

   public IdleBehaviorRealizer (IdleBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.idle(getParams().isEnable());
      fireDoneMessage();
   }
}
