package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class RagFaceExpressionRealizer extends SingleRunPrimitiveRealizer<FaceExpressionBehavior> {

	private final RagClientProxy proxy;

	public RagFaceExpressionRealizer(FaceExpressionBehavior params, RagClientProxy proxy) {
		super(params);
		this.proxy = proxy;
	}

	@Override
	protected void singleRun() {
		proxy.express(getParams().getExpression());
		fireDoneMessage();
	}

}
