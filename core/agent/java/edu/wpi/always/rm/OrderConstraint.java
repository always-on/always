package edu.wpi.always.rm;


// This constraint indicates that firstTask must occur BEFORE secondTask.
public class OrderConstraint extends PlanningConstraint {

	String firstTask;
	String secondTask;
	
	public OrderConstraint(String taskA, String taskB){
		firstTask = taskA;
		secondTask = taskB;
	}
	
	public String toString(){
		return firstTask + " must be performed before " + secondTask;
	}
	
}
