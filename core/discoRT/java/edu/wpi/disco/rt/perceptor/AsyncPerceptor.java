package edu.wpi.disco.rt.perceptor;

public interface AsyncPerceptor<T extends Perception> extends Perceptor<T> {

   void addPerceptorListener (AsyncPerceptorListener<T> listener);

   void removePerceptorListener (AsyncPerceptorListener<T> listener);
}
