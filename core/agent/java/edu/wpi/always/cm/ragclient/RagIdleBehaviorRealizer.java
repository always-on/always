package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class RagIdleBehaviorRealizer extends SingleRunPrimitiveRealizer<IdleBehavior> {

	private final RagClientProxy proxy;

	public RagIdleBehaviorRealizer(IdleBehavior params, RagClientProxy proxy) {
		super(params);
		this.proxy = proxy;
	}

	@Override
	protected void singleRun() {
		proxy.idle(getParams().isEnable());
		fireDoneMessage();
	}

}
