package edu.wpi.always.rm.plugin;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import edu.wpi.always.rm.*;
import edu.wpi.always.rm.RelationshipManager.relationshipStage;

public class StudyPlugin extends ActivityPlugin {

	//public Activity(String actName, double socialVal, double virtueVal, double durationVal, 
		//	double buildupVal, TaskModel model){
	
	ArrayList<Activity> activities;
	
	public StudyPlugin(){
		File demoModelFile = new File("Models/StudyTasks.xml");
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder;
		activities = new ArrayList<Activity>();
		try {
			docBuilder = docBuildFactory.newDocumentBuilder();
			Document demoModelDoc = docBuilder.parse(demoModelFile);
			//TODO: prevent multi-loading
			
			activities.add(new Activity("Introduction",  1, 3, 1, 0, demoModelDoc, this, 
					"study", "ns", "they talk about the basic services a community worker provides")); 
			activities.add(new Activity("Baseball",      2, 1, 1, 0, demoModelDoc, this, 
					"study", "ns", "they talk about a recent baseball game")); 
			activities.add(new Activity("Weather",       1, 1, 1, 0, demoModelDoc, this, 
					"study", "ns", "they talk about the weather")); 
			activities.add(new Activity("CardGame",      2, 1, 2, 0, demoModelDoc, this, 
					"study", "ns", "they play a card game together"));
			activities.add(new Activity("Television",    1, 0, 1, 0, demoModelDoc, this, 
					"study", "ns", "they watch TV together"));
			activities.add(new Activity("Anecdotes",     2, 1, 1, 2, demoModelDoc, this, 
					"study", "ns", "they trade humorous anecdotes"));
			activities.add(new Activity("Politics",      2, 2, 2, 2, demoModelDoc, this, 
					"study", "ns", "they discuss the mayoral election")); 
			activities.add(new Activity("Books",         4, 1, 2, 3, demoModelDoc, this, 
					"study", "ns", "they talk about some of their facorite books"));
			activities.add(new Activity("Story",         4, 2, 2, 3, demoModelDoc, this, 
					"study", "ns", "Katherine tells some stories from her childhood"));
			activities.add(new Activity("Schedule",      1, 3, 1, 3, demoModelDoc, this, 
					"study", "ns", "they organize Katherine's schedule"));
			activities.add(new Activity("Exercise",      2, 5, 3, 3, demoModelDoc, this, 
					"study", "ns", "they do an exercise routine together"));
			activities.add(new Activity("Family",        3, 2, 1, 4, demoModelDoc, this, 
					"study", "ns", "they talk about Katherine's family"));
			activities.add(new Activity("Pills",         1, 3, 1, 4, demoModelDoc, this, 
					"study", "ns", "they sort out Katherine's medicine")); 
			activities.add(new Activity("Scrapbook",     3, 2, 2, 5, demoModelDoc, this, 
					"study", "ns", "they work on Katherine's scrapbook"));
			activities.add(new Activity("SocialMedia",   3, 2, 2, 5, demoModelDoc, this, 
					"study", "ns", "they read emails from Katherine's family and write replies"));
			activities.add(new Activity("PersonalTalk",  6, 1, 2, 7, demoModelDoc, this, 
					"study", "ns", "they talk about how Katherine felt after her parents' death")); //never used in study
			activities.add(new Activity("Convince",      1, 5, 1, 7, demoModelDoc, this, 
					"study", "ns", "Samantha convinces Katherine to write a letter she's been putting off"));	
			activities.add(new Activity("FamilyIllness", 4, 4, 2, 8, demoModelDoc, this, 
					"study", "ns", "they discuss Katherine's brother's illness"));
			activities.add(new Activity("Illness",       3, 6, 2, 8, demoModelDoc, this, 
					"study", "ns", "they talk about Katherine's recent diagnosis with a serious illness"));

			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initial(RelationshipManager RM){
		
		// all done in constructor now... rework plugin structure??
	
	}
	
	public void update(RelationshipManager RM){

		double cutoff;
		if(RM.currentStage == relationshipStage.STRANGERS){
			cutoff = 2;
		}else if(RM.currentStage == relationshipStage.ACQUAINTANCES){
			cutoff = 6;
		}else{
			cutoff = 10;
		}
		
		for(Activity act : activities){
			if(((act.buildup <= cutoff) && (act.name != "Introduction")) 
					|| ((RM.currentStage == relationshipStage.STRANGERS) && (act.name == "Introduction"))){
				RM.addActivity(act);
			}
		}
		
		
	}

}
