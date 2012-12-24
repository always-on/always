package edu.wpi.always.cm.perceptors.async;

import edu.wpi.always.cm.Perception;

public interface AsyncPerceptorListener<T extends Perception> {

   void onPerception (T perception);
}
