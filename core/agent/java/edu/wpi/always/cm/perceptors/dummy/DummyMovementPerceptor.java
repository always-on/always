package edu.wpi.always.cm.perceptors.dummy;

import java.awt.*;

import org.joda.time.*;

import edu.wpi.always.cm.perceptors.*;

public class DummyMovementPerceptor implements MovementPerceptor {

	volatile MovementPerception latest;

	@Override
	public MovementPerception getLatest() {
		return latest;
	}

	@Override
	public void run() {
		latest = new MovementPerception() {

			DateTime d = DateTime.now();

			@Override
			public DateTime getTimeStamp() {
				return d;
			}

			@Override
			public Point movementLocation() {
				return new Point(10, 10);
			}
		};
	}

}
