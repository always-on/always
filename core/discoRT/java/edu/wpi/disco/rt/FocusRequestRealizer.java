package edu.wpi.disco.rt;

import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class FocusRequestRealizer extends
      SingleRunPrimitiveRealizer<FocusRequestBehavior> {

   public FocusRequestRealizer (FocusRequestBehavior params) {
      super(params);
   }

   @Override
   protected void singleRun () {
      fireDoneMessage();
   }
}
