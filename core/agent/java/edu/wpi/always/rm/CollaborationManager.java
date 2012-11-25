package edu.wpi.always.rm;

import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.wpi.always.DiscoSynchronizedWrapper;
import edu.wpi.always.rm.RelationshipManager.relationshipStage;
import edu.wpi.disco.*;
import org.w3c.dom.Document;



public class CollaborationManager extends Thread{
	
	RelationshipManager relationshipModule;
	
	ConcurrentLinkedQueue<PlanningMessage> planBuffer;
	
	public CollaborationManager(){
		relationshipModule = new RelationshipManager(this);
	}
	
	public CollaborationManager(RelationshipManager rm){
		relationshipModule = rm;
	}
	
	public void sendPlan(Document plan){};
	
	public void run(){
		
		relationshipModule.currentStage = relationshipStage.ACQUAINTANCES;
		relationshipModule.baseCloseness = 3; //TODO: enh
		relationshipModule.stockedSocial = 99;
		relationshipModule.currentCloseness = relationshipModule.baseCloseness;
		relationshipModule.closenessTime = new Date();
		
		Document plan = relationshipModule.getLatestPlanInDoc();
		
		Interaction interaction = new Interaction(
		            new Agent("agent"), 
		            new User("user"),
		            null);
		
		// TODO: Add preloads again! This should work with the rearranged disco.
		//ArrayList<Document> preloads = relationshipModule.plan().preload; ?
	
		Disco disco = interaction.getDisco();
		
		interaction.start(true);

		disco.load(plan, null);
		
		try {
			interaction.join();
		} catch (InterruptedException e) {}	
		
		float closeness=0;
		
		int time = 3;
		
		relationshipModule.afterInteraction(new DiscoSynchronizedWrapper(disco), closeness, time);
		
		
	}
	
	
	@SuppressWarnings("unused")
	private void execute(PlanningMessage plan) {
		
		
		System.out.println("Talking about weather"); //refer to WeatherPlugin
		relationshipModule.report(null);
		
	}


	public void planTasks(PlanningMessage message){
		planBuffer.add(message);
		//System.out.println(message);
	}
	
	public void setRelationshipModule(RelationshipManager RM){
		relationshipModule = RM;
	}
	
	
	

}
