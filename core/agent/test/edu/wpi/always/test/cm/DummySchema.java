package edu.wpi.always.test.cm;

import edu.wpi.always.cm.*;

public class DummySchema extends SchemaImplBase {

	public DummySchema(BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory resourceMonitor) {
		super(behaviorReceiver, resourceMonitor);
	}

	@Override
	public void run() {
	}

}
