package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.*;


public class WeatherPlugin extends ConversationPlugin{
	
	// sends metadata to relationship module (as an ActivityPlugin)
	
	// Sends actions to DM
	
	// receives reactions from DM
	
	// receives commands from RM

	
	
	// Creates a list of interesting topics
	// reports on today's weather when activated
	

	//report on today's weather
	
	public WeatherPlugin(){
		
	}
	
	
	// replace this! needs generalized interface.
	public static void weatherReport(){}
	
	
	
	public void initial(RelationshipManager RM) {
		
		RM.addActivity(new Activity("TalkWeather", 1.0, null, this));
		
	}
	
	
	public void update(RelationshipManager RM){
		
	}
	

	
	public String toString(){
		return "Weather ClientPlugin";
	}
	
	public void report(){
		
	}
	
	
	
	// GET weather report from net connection
	
	// DO "report weather" task (param: today's weather)
	
	// NOTHING ELSE??!!
	
	
	
	
	// TODO: "list of interesting things"
	
	// This should be sending tasks to the relationship module which can deal with them,
	// attaching data and so forth (tho send relevant info too!). 
	
	
}
