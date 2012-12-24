package edu.wpi.always.test.cm;

import java.awt.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;

public class FakeGazeSchema extends SchemaImplBase {
	final Point point;

	public FakeGazeSchema(BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory resourceMonitor, Point fixedGazePoint) {
		super(behaviorReceiver, resourceMonitor);
		point = fixedGazePoint;
	}

	@Override
	public void run() {
		propose(new GazeBehavior(point), 1);
	}

}
