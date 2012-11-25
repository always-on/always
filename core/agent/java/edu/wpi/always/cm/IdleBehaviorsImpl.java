package edu.wpi.always.cm;

import java.awt.*;
import java.util.*;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.cm.realizer.*;

public class IdleBehaviorsImpl implements IdleBehaviors {

	@Override
	public PrimitiveBehavior get (Resource resource) {
		switch (resource) {
		case Menu:
			return new MenuBehavior(new ArrayList<String>());
		case FaceExpression:
			return new FaceExpressionBehavior(AgentFaceExpression.Warm);
		case Gaze:
			return new GazeBehavior(new Point(0, 0));//Center face
		case Idle:
			return new IdleBehavior(true);
		default:
			return null;
		}
	}

}
