package edu.wpi.always.cm.perceptors.dummy;

import org.joda.time.*;

import edu.wpi.always.cm.perceptors.*;

public class DummyMotionPerceptor implements MotionPerceptor {

	private MotionPerceptionImpl latest;

	@Override
	public MotionPerception getLatest() {
		return latest;
	}

	@Override
	public void run() {
		latest = new MotionPerceptionImpl(DateTime.now(), false);
	}

}
