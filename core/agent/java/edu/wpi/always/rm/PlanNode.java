package edu.wpi.always.rm;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

public class PlanNode {
	private Activity activity;
	private double duration;
	private PlanNode parent;
	private ArrayList<PlanNode> children;
	private double closeness;
	private double utility;
//	private double maxUtility;
	private double virtue;
	private double personalUtility;
	
	public PlanNode(RelationshipManager RM){
		activity = null;
		duration = 0;
		parent = null;
		children = new ArrayList<PlanNode>();
		closeness = RM.currentCloseness;
		utility = 0;
		personalUtility = 0;
		virtue = 0;
	}
	
	public PlanNode(Activity myActivity, RelationshipManager RM){
		activity = myActivity;
		duration = myActivity.duration;
		parent = null;
		children = new ArrayList<PlanNode>();
		
		closeness = myActivity.social; 
		virtue = myActivity.virtue;
		
		if(RM.activityOccurrences.containsKey(myActivity.name)  && (RM.activityOccurrences.get(myActivity.name) != null)){
			long elapsedMins = (new Date().getTime() - (long)(myActivity.duration * Activity.DURATIONUNIT * 60000)
					- RM.activityOccurrences.get(myActivity.name).date.getTime()) / 60000;
			elapsedMins = Math.max(elapsedMins, 60);
			double coefficient = (1 - (.99 / (Math.pow(elapsedMins/60, 0.5) )));
			closeness *= coefficient;
			virtue *= coefficient;
			}
		
		utilityDamping(RM.activityOccurrences); 
		
		utility = closeness + virtue;
		personalUtility = utility;
		
		closeness += RM.currentCloseness;
		//maxUtility = utility;
	}
	
	public PlanNode(Activity myActivity, PlanNode myParent, RelationshipManager RM){
		activity = myActivity;
		duration = myParent.getDuration() + myActivity.duration;
		parent = myParent;
		myParent.addChild(this);
		children = new ArrayList<PlanNode>();
		
		closeness = myActivity.social;
		virtue = myActivity.virtue;
		
		//Hashtable<String, Occurrence> projectedHistory = projectHistory(RM);

		long now = new Date().getTime() + (long)(myParent.getDuration() * Activity.DURATIONUNIT * 60000);
		
		/*
		if(projectedHistory.containsKey(myActivity.name) && (projectedHistory.get(myActivity.name) != null)){
			long elapsedMins = Math.max((now - (long)(myActivity.duration * Activity.DURATIONUNIT * 60000) 
					- projectedHistory.get(myActivity.name).date.getTime()) / 60000, 1);
			elapsedMins = Math.max(elapsedMins, 60);
			double coefficient = (1 - (.9 / (Math.pow(elapsedMins/60, 0.5) )));
			//System.out.println(coefficient);
			closeness *= coefficient;
			virtue *= coefficient;
			
			
			if(!matchAncestor(myActivity.name)){
				System.out.println("Mismatch:");
				System.out.println("Proj. History:");
				for(String s : projectedHistory.keySet()){
					System.out.print(s + " ");
				}
				System.out.println();
				System.out.println("actual history:");
				if(hasParent() && (parent.activity != null)){
					System.out.print(parent.activity.name);
					if(parent.hasParent() && (parent.parent.activity != null)){
						System.out.print(parent.parent.activity.name);
					}
				}
				System.out.println();
			}
		}*/
		
		if(matchAncestor(myActivity.name)){
			long elapsedMins = (long)((duration - getDurationTil(myActivity.name)) * Activity.DURATIONUNIT);
			//long elapsedMins = Math.max((now - (long)(myActivity.duration * Activity.DURATIONUNIT * 60000) 
				//	- projectedHistory.get(myActivity.name).date.getTime()) / 60000, 1);
			elapsedMins = Math.max(elapsedMins, 60);
			double coefficient = (1 - (.9 / (Math.pow(elapsedMins/60, 0.5) )));
			//System.out.println(coefficient);
			closeness *= coefficient;
			virtue *= coefficient;
		}
		
		//utilityDamping(projectedHistory); 
		
		//negatepriors();
		
		utility = closeness + virtue;
		personalUtility = utility;
		
		closeness += myParent.getCloseness();
		virtue += myParent.getVirtue();
		utility += myParent.getUtility();
		
		//maxUtility = utility;
	}
	
	private double getDurationTil(String name){
		if(hasParent() && (parent.activity != null)){
			if(parent.activity.name == name){
				return parent.duration;
			}
			return parent.getDurationTil(name);
		}
		return 0;//err
	}
	
	@SuppressWarnings("unused")
	private void negatepriors(){
		if(matchAncestor(activity.name)){
			closeness = .01 * activity.social;
			virtue = .01 * activity.virtue;
		}
	}
	
	private boolean matchAncestor(String name){
		if(hasParent() && (parent.activity!= null)){
			if(parent.activity.name == name){
				return true;
			}else{
				return parent.matchAncestor(name);
			}
		}
		return false;
	}
	
	
//	@SuppressWarnings("unchecked")
	@SuppressWarnings("unused")
	private Hashtable<String, Occurrence> projectHistory(RelationshipManager RM){
		return populateHistory(new Hashtable<String, Occurrence>(RM.activityOccurrences));
	}
	
	private void utilityDamping(Hashtable<String, Occurrence> history){
		double socialDamper = 1;
		double virtueDamper = 1;
		for(String activity : history.keySet()){
			Occurrence occ = history.get(activity);
			long elapsedMins = Math.max((new Date().getTime() - (long)(getActivity().duration * Activity.DURATIONUNIT * 60000) 
					- occ.date.getTime()) / 60000, 1);
			socialDamper *= (1 - ((history.get(activity).social * .005)/(elapsedMins)));
			virtueDamper *= (1 - ((history.get(activity).virtue * .005)/(elapsedMins)));
		}
		closeness *= socialDamper;
		virtue *= virtueDamper;
	}
	
	//Seems to be broken. //*************************?? check la
	public Hashtable<String, Occurrence> populateHistory(Hashtable<String, Occurrence> history){
		
		if(activity != null){
			long now = new Date().getTime();
			if(hasParent() && (getParent().activity!= null)){
				now += (getParent().duration * Activity.DURATIONUNIT * 60000);
			}
			history.put(activity.name, new Occurrence(new Date(now), activity));
		}
		
		if(hasParent() && (getParent().activity != null)){
			history = getParent().populateHistory(history);
		}
				
		return history;
	}
	
	public void addChild(PlanNode newChild){
		children.add(newChild);
	}
	
	public void removeChild(PlanNode child){
		children.remove(child); 
	}
	
	public boolean hasChildren(){
		if (children.isEmpty()){
			return false;
		}else{
			return true;
		}
	}
	
	public double getCloseness(){
		return closeness;
	}
	
	public double getDuration(){
		return duration;
	}
	
	public double getUtility(){
		return utility;
	}
	
	public double getVirtue(){
		return virtue;
	}
	
	public boolean hasParent(){
		if(this.parent == null){
			return false;		
		}else{
			return true;
		}
	}
	
	public double getMaxUtility(){
		double maxOfChildren = 0;
		double currentMax = 0;
		for(PlanNode child : children){
			currentMax = child.getMaxUtility();
			if (currentMax > maxOfChildren){
				maxOfChildren = currentMax;
			}
		}
		if(activity==null){
			return maxOfChildren;
		}else{
			return personalUtility + maxOfChildren;
		}
	}
	
	// These antisocial utility functions are obsolete. If necessary, they should use personalUtility instead of directly accessing child utilities.
	public double getMaxAntisocialUtility(){ 
		double maxOfChildren = 0;
		double currentMax = 0;
		for(PlanNode child : children){
			currentMax = child.getMaxAntisocialUtility();
			if (currentMax > maxOfChildren){
				maxOfChildren = currentMax;
			}
		}
		
		if(activity==null){
			return maxOfChildren;
		}else{
			return activity.virtue - activity.social + Math.max(0, activity.buildup - getExistingBuildup()) + maxOfChildren;
		}
	}
	
	public double getAntisocialUtility(){
		if(this.hasParent()){
			return activity.virtue - activity.social + Math.max(0, activity.buildup - getExistingBuildup()) + parent.getAntisocialUtility();
		}else if(this.activity != null){
			return activity.virtue - activity.social + Math.max(0, activity.buildup - getExistingBuildup());
		}else{
			return 0;
		}
	}
	
	public double getExistingBuildup(){
		if(this.hasParent()){
			return parent.getCloseness() + parent.getExistingBuildup();
		}
		else{
			return 0;
		}
	}
	
	public double getMaxUnpleasantUtility(){
		double maxOfChildren = 0;
		double currentMax = 0;
		for(PlanNode child : children){
			currentMax = child.getMaxUnpleasantUtility();
			if (currentMax > maxOfChildren){
				maxOfChildren = currentMax;
			}
		}
		if(activity==null){
			return maxOfChildren;
		}else{
			return activity.virtue + activity.buildup - getExistingBuildup() + maxOfChildren;
		}
	}
	
	public double getUnpleasantUtility(){
		if(this.hasParent()){
			return activity.virtue + activity.buildup - getExistingBuildup() + parent.getAntisocialUtility();
		}else if(this.activity != null){
			return activity.virtue + activity.buildup - getExistingBuildup();
		}else{
			return 0;
		}
	}
	
	public double getMaxVirtue(){
		double maxOfChildren = 0;
		double currentMax = 0;
		for(PlanNode child : children){
			currentMax = child.getMaxVirtue();
			if (currentMax > maxOfChildren){
				maxOfChildren = currentMax;
			}
		}
		if(activity==null){
			return maxOfChildren;
		}else{
			return activity.virtue + maxOfChildren;
		}
	}
	
	public ArrayList<PlanNode> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<PlanNode> childrenVal){
		children = childrenVal;
	}

	public PlanNode getParent(){
		return parent;
	}
	
	public double totalDuration(){
		double total = duration;
		if(parent != null){
			total += parent.totalDuration();
		}
		return total;
	}
	
	public Activity getActivity(){
		return activity;
	}
}
