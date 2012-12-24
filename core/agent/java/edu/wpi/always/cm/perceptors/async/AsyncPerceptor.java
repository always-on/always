package edu.wpi.always.cm.perceptors.async;

import edu.wpi.always.cm.*;

public interface AsyncPerceptor<T extends Perception> extends Perceptor<T> {

   void addPerceptorListener (AsyncPerceptorListener<T> listener);

   void removePerceptorListener (AsyncPerceptorListener<T> listener);
}
