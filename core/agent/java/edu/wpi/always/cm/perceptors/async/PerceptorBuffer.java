package edu.wpi.always.cm.perceptors.async;

import java.util.*;
import java.util.concurrent.*;

import edu.wpi.always.cm.*;

public class PerceptorBuffer<T extends Perception>{
	private Queue<T> perceptionStack = new ConcurrentLinkedQueue<T>();
	
	public void push(T perception){
		perceptionStack.add(perception);
	}
	
	public T next() {
		return perceptionStack.poll();
	}
	
}
