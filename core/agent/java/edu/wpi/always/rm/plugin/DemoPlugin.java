package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.*;

public class DemoPlugin extends ActivityPlugin {

	//public Activity(String actName, double socialVal, double virtueVal, double durationVal, 
		//	double buildupVal, TaskModel model){
	
	public void initial(RelationshipManager RM){
		
		//TaskModel demoModel = new TaskModel("Models/DemoTasks.xml");
		
		RM.addActivity(new Activity("MindlessChatter",   5, 1, 2, 0, null, this, "demo", "ns", "talk about nothing in particular")); 
		RM.addActivity(new Activity("FaffAbout",         1, 1, 1, 0, null, this, "demo", "ns", "just faff about for a while"));	
		RM.addActivity(new Activity("PersonalQuestions", 3, 8, 2, 4, null, this, "demo", "ns", "discuss personal topics"));
		RM.addActivity(new Activity("DailyPushups",      1, 6, 3, 0, null, this, "demo", "ns", "perform an exercise routine")); //TODO:ns
	}

}
