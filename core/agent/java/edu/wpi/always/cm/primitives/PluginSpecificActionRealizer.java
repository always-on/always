package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class PluginSpecificActionRealizer extends
      SingleRunPrimitiveRealizer<PluginSpecificBehavior> {

   public PluginSpecificActionRealizer (PluginSpecificBehavior params) {
      super(params);
   }

   @Override
   protected void singleRun () {
      getParams().getPlugin().doAction(getParams().getActionName());
      fireDoneMessage();
   }
}
