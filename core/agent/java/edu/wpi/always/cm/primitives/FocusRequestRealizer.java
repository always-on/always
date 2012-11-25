package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.realizer.*;

public class FocusRequestRealizer extends SingleRunPrimitiveRealizer<FocusRequestBehavior> {

	public FocusRequestRealizer(FocusRequestBehavior params) {
		super(params);
	}

	@Override
	protected void singleRun() {
		fireDoneMessage();
	}

}
