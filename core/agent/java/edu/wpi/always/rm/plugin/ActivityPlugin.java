package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.RelationshipManager;


//import RelationshipModule.relationshipStage;

// Superclass of all plugins, at top of plugin hierarchy	

public abstract class ActivityPlugin {
	
	public enum arcType{
		LINEAR, 
	}
	
	
	// TODO: activity metadata data types etc. 
	
	

	//private relationshipStage minStage;
	private int minCloseness;

	
	//send updated activities to RM 
	public void update(RelationshipManager RM) {
		
	}
	
	//send initial activities to RM
	public void initial(RelationshipManager RM) {
		
	}
	
	
	// translate task stuff to relationship stuff? to what degree? how task-specific is this?
	// the Activity ClientPlugin is stored somewhere by the program performing the activity. It uses
	// this to call the relevant functions (written here).
	
	
	
	
	// getters/setters
	public int getMinCloseness() {
		return minCloseness;
	}

	public void setMinCloseness(int minCloseness) {
		this.minCloseness = minCloseness;
	}


}
