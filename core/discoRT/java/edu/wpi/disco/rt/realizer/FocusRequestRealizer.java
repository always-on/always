package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.behavior.FocusRequestBehavior;

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
