package edu.wpi.always.rm;

public class Planner {
	
	
	public void makePlans(){
		
		//JTree tree;
		
	}

}




/*


}

class PossiblePlan {
	ArrayList<Activity> plan;
	double utility;
}

//returns bestplan and Double maxutility //TODO: damping mods
private PossiblePlan makePlans(ArrayList<Activity> plan, double currentDuration, double currentUtility, 
		                 ArrayList<Activity> bestPlan, double maxDuration, double maxUtility){

	boolean leaf = true;
	for(Activity activity : activityQueue){
		if((currentDuration + activity.duration <= maxDuration) & isAccessable(activity, plan)){ 
			
			leaf = false;
			
			ArrayList<Activity> newPlan = cloneArrayList(plan);
			newPlan.add(activity);
			
			PossiblePlan result = makePlans(newPlan, currentDuration + activity.duration, 
					           currentUtility + activity.social + activity.virtue,
					           bestPlan, maxDuration, maxUtility);
			
			bestPlan = result.plan;
			maxUtility = result.utility;
			
		}
	}
	
	
	PossiblePlan returnVal = new PossiblePlan();
	
	//TODO: correct &&&?
	if(leaf & (currentUtility > maxUtility)){
		returnVal.plan = plan;
		returnVal.utility = currentUtility;
	}
	else{
		returnVal.plan = bestPlan;
		returnVal.utility = maxUtility;
	}
	
	return returnVal;
	
}


*/