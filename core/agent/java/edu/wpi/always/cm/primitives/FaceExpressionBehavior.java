package edu.wpi.always.cm.primitives;

import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class FaceExpressionBehavior extends PrimitiveBehavior {

	private final AgentFaceExpression expression;

	public FaceExpressionBehavior(AgentFaceExpression expression) {
		this.expression = expression;
	}

	@Override
	public Resource getResource() {
		return Resource.FaceExpression;
	}

	public AgentFaceExpression getExpression() {
		return expression;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof FaceExpressionBehavior))
			return false;

		FaceExpressionBehavior theOther = (FaceExpressionBehavior) o;
		return theOther.getExpression().equals(this.getExpression());
	}

	@Override
	public int hashCode() {
		return getExpression().hashCode();
	}

	@Override
	public String toString() {
		return "FaceExpression(" + expression + ')';
	}

}
