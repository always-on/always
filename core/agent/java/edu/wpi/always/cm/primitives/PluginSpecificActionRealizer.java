package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.realizer.*;

public class PluginSpecificActionRealizer extends SingleRunPrimitiveRealizer<PluginSpecificBehavior> {

	public PluginSpecificActionRealizer (PluginSpecificBehavior params) {
		super(params);
	}

	@Override
	protected void singleRun () {
		getParams().getPlugin().doAction(getParams().getActionName());
		fireDoneMessage();
	}

}
