package edu.wpi.always.cm.perceptors.async;

import edu.wpi.always.cm.Perception;
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
