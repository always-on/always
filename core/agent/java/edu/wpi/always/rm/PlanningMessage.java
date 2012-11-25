package edu.wpi.always.rm;


import java.util.ArrayList;

import org.w3c.dom.Document;



public class PlanningMessage {
	
	Document taskModel;
	ArrayList<Document> preload;
	
	public PlanningMessage(Document model, ArrayList<Document> pre){
		taskModel = model;
		preload = pre;
		// properties...
	}
	
	public PlanningMessage(Document model){
		taskModel = model;
		preload = new ArrayList<Document>();
	}

	
	
	/*

	Collection<String> tasks;
	Collection<PlanningConstraint> constraints;
	ArrayList<ActivityPlugin> plugins; // for now... need to link them to tasks

	//TODO: link tasks to plugins... :\ ???
	
	
	
	
	public void addTask(String task){
		tasks.add(task);
	}
	
	public void addConstraint(PlanningConstraint constraint){
		constraints.add(constraint);
	}

	
	public void addRestriction(Task taska, Task taskb, Restriction.RestrictionType rtype){
		restrictions.add(new Restriction(taska, taskb, rtype));
	}
	
	
	public String toString(){
		String order = "Perform ";
		
		for (String task : tasks){
			order.concat(task + ", ");
		}
		
		if(!constraints.isEmpty()){
			order.concat("with the constraint(s): ");
			
			for(PlanningConstraint constraint : constraints){
				order.concat(constraint.toString() + ", ");
			}
			
		}
		
		order.concat( "and no additional constraints.");

		return order;
	}
	
	
	*/
	
	
	
	
	
}
                                                                                                                                                                                                