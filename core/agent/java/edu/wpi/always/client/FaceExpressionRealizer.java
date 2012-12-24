package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class FaceExpressionRealizer extends SingleRunPrimitiveRealizer<FaceExpressionBehavior> {

	private final ClientProxy proxy;

	public FaceExpressionRealizer(FaceExpressionBehavior params, ClientProxy proxy) {
		super(params);
		this.proxy = proxy;
	}

	@Override
	protected void singleRun() {
		proxy.express(getParams().getExpression());
		fireDoneMessage();
	}

}
