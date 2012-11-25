package edu.wpi.always.cm.perceptors.async;

import edu.wpi.always.cm.*;

public interface AsyncPerceptorListener<T extends Perception> {
	void onPerception(T perception);
}
