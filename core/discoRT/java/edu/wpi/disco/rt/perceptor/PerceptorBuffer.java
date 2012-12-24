package edu.wpi.disco.rt.perceptor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PerceptorBuffer<T extends Perception> {

   private Queue<T> perceptionStack = new ConcurrentLinkedQueue<T>();

   public void push (T perception) {
      perceptionStack.add(perception);
   }

   public T next () {
      return perceptionStack.poll();
   }
}
