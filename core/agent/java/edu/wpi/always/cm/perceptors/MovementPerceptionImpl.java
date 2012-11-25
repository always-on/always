package edu.wpi.always.cm.perceptors;

import java.awt.*;

import org.joda.time.*;

public class MovementPerceptionImpl implements MovementPerception {

	private final DateTime stamp;
	private final Point location;

	public MovementPerceptionImpl(DateTime t, Point location) {
		this.stamp = t;
		this.location = location;

	}

	@Override
	public DateTime getTimeStamp() {
		return stamp;
	}

	@Override
	public Point movementLocation() {
		return location;
	}

}
