package edu.wpi.always.rm;

// This constraint indicates that the secondaryTask must be performed DURING the primaryTask.
public class SimulConstraint extends PlanningConstraint {
	
	String primaryTask;
	String secondaryTask;

	public SimulConstraint(String taskA, String taskB){
		primaryTask = taskA;
		secondaryTask = taskB;
	}
	
	public String toString(){
		return secondaryTask + " must be performed during " + primaryTask;
	}
	
}
