package edu.wpi.always.cm.perceptors;

import org.joda.time.*;

public class MotionPerceptionImpl implements MotionPerception {

	private final DateTime stamp;
	private final boolean motion;

	public MotionPerceptionImpl(DateTime t, boolean motion) {
		this.stamp = t;
		this.motion = motion;

	}

	@Override
	public DateTime getTimeStamp() {
		return stamp;
	}

	@Override
	public boolean hasMovement() {
		return motion;
	}

}
