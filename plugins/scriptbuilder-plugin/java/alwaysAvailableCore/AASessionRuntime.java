package alwaysAvailableCore;


import java.sql.ResultSet;
import java.io.*;
import java.sql.SQLException;

import DialogueRuntime.*;

/** This class implements server-side services for HB project, such as makePlot() 
 * and  saveSteps(), etc.
 */

public class AASessionRuntime extends  SessionRuntime{

	public AASessionRuntime(PersistentStore s, PropertiesInitializer i) {
			super(s,i);
	}
	
	
	/*
	
	//FIXME: we may need to update some of these methods to refer to the DBstore (see the compas project for a reference). 
 
	// there's no reason to cache the steps since they are already in the properties object
	public void CACHE_STEPS(DialogueStateMachine _DSM_) {

	    }

	// This method is used by DialogSession to save the steps returned
	// from the pedometer
	public  void saveSteps(int studyday, int steps){
	  	recordSteps(studyday, steps); 
	}

	public  void recordSteps(int studyday, int steps){
		userProperties.setProperty("PA_BEHAVIOR_"+studyday,  "" +steps);
		
	}

	public  void recordSteps(int steps){
		   	int studyday = store.getUsersStudyDay();
	       recordSteps(studyday, steps);
	   	}

		/* Primitive to access steps information for the current user.
	     *  Probably implemented by caching all user steps for user at startup
	     *  into an array, then providing a local index. Returns UNDEFINED for
	     *  undefined days.
	     *  
	     *  For now, assumes steps are stored in PA_BEHAVIOR_<studyday> properties,
	     *  per upitt medtrack. This can be updated as needed - just assumes that
	     *  we can cheaply query many individual days of steps. However, for
	     *  for ElderWalk/tablet at least, will need to keep them here.
	     *
	    public int STEPS(int studyDay,DialogueStateMachine _DSM_) throws Exception {
	      if(studyDay<=0) return -1;
	      int steps =  getSteps(studyDay);
	      System.out.println("Steps for day " + studyDay + " were " + steps);
	      return (steps);
	    }

	    public int getSteps(int dayToGet) {
	       	int studyday = store.getUsersStudyDay();
	    	int returnval = -1;
	    		if (dayToGet > studyday) {
	    			returnval = -1;
	    		} else {
	    			String steps = userProperties.getProperty("PA_BEHAVIOR_"+dayToGet);
	    			try {
	    				returnval = Integer.parseInt(steps);
	    			} catch (NumberFormatException e) {
	    				returnval = -1;
	    			}
	    		}
	    	return returnval;
	    }
	    
	public int[] getStepsCache(){
		int studyDay=store.getUsersStudyDay();
		int[] stepsCache=new int[studyDay+1];
		for(int i=0;i<stepsCache.length;i++){
			stepsCache[i]=-1;
		}	
		for (int i = 1; i < studyDay; i++) {
				String steps = userProperties.getProperty("PA_BEHAVIOR_"+i);
				if (!(UNDEFINED(steps))) {
					stepsCache[i]= Integer.parseInt(steps);
	    		}	
	
		}
		return stepsCache;
	}

	// nothing to do
	public void updateCache(int day, int steps) {
		
	}


	public static boolean UNDEFINED(String value) {
		return value == null || value.trim().length() == 0;
	    }

	// nothing to do because steps and goals are already in properties
	public void cacheStepsAndGoals() {
			
		}

	// nothing to do here
	public void clearCache() {
		   System.out.println("Clearing the steps and goals cache");
	}

	public int getGoal(int studyDay) {
		int thisDay=store.getUsersStudyDay();
		if ((studyDay < 1) ||
			 (studyDay > thisDay)) {
			return -1;
		} else {
			String goalsteps = userProperties.getProperty("PA_GOAL_"+studyDay);
			if (!(UNDEFINED(goalsteps))) {
				return -1;
			} else { 
				return(Integer.parseInt(goalsteps));
			}
		}
	}

	public int[] getGoalsCache(){
		int studyDay=store.getUsersStudyDay();
		int[] goalsCache=new int[studyDay+1];
		for(int i=0;i<goalsCache.length;i++){
					goalsCache[i]=-1;
		}	
		for (int i = 1; i < studyDay; i++) {
			String goalsteps = userProperties.getProperty("PA_GOAL_"+i);
			if (!(UNDEFINED(goalsteps))) {
				goalsCache[i]= Integer.parseInt(goalsteps);
			}	
		}
		return goalsCache;
	}
 
	public void recordGoal(int stepsPerDay) {
		int studyDay=store.getUsersStudyDay();
		userProperties.setProperty("PA_GOAL_"+studyDay, ""+stepsPerDay);
	}

	/** Another stub - placeholder that will be used to store daily step goals in the DB
	for plotting. Use GETINT("STUDY_DAY") for day index. May record '-1' for undefined or cleared goal. 
	*
	public void recordGoal(DialogueStateMachine _DSM_,int stepsPerDay) throws Exception {
	System.out.println("RECORDING GOAL: starting on study day "+
			   userProperties.getProperty("STUDY_DAY")+" goal is "+stepsPerDay+
			   " steps per day.");
	recordGoal(stepsPerDay);
	}

	public void updateGoalCache(int day, int goal) {
	 // nothing to do
	}
	
	*/

}