package edu.wpi.disco.rt.perceptor;

public abstract class PerceptorBase<T extends Perception> implements Perceptor<T> {
   
   // volatile because accessed by run() method on perceptor thread
   // and by getLatest() method on schema threads
   protected volatile T latest;
   
   @Override
   public T getLatest () { return latest; }

   @Override
   public void run () {}
}
