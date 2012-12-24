package edu.wpi.disco.rt.perceptor;


public interface AsyncPerceptorListener<T extends Perception> {

   void onPerception (T perception);
}
