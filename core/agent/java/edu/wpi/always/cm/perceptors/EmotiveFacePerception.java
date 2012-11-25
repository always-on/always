package edu.wpi.always.cm.perceptors;

import java.awt.*;

import edu.wpi.always.cm.*;

public interface EmotiveFacePerception extends Perception {

	int getHappiness();
	int getBottom();
	int getTop();
	int getLeft();
	int getRight();
	boolean hasFace();
	boolean isNear();
	Point getLocation();
}
