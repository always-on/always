package edu.wpi.always.rm.plugin;

import edu.wpi.always.rm.*;

// TODO: use this to update conversationplugin and activityplugin with methods (also see weather)


public class BaseballPlugin extends ConversationPlugin{
	
	public BaseballPlugin(){
	}
	
	public void initial(RelationshipManager RM){
		
		RM.addActivity(new Activity("TalkBaseball", 0.8, null, this)); 
		
	}

	public void update(RelationshipManager RM){
		
	}
	
	
	public String toString(){
		return "Baseball ClientPlugin";
	}
	
	public void report(){
			
	}
	
	
	
}
