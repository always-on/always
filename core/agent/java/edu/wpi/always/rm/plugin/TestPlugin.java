package edu.wpi.always.rm.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.w3c.dom.Document;

//import edu.wpi.always.owl.Ontology;
import edu.wpi.always.rm.*;

public class TestPlugin extends ActivityPlugin {
	
	ArrayList<Activity> activities;
	
	public TestPlugin(){
		
		File demoModelFile = new File("Models/TestTasks.xml");
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder;
		activities = new ArrayList<Activity>();
		try {
			docBuilder = docBuildFactory.newDocumentBuilder();
			Document demoModelDoc = docBuilder.parse(demoModelFile);
			//TODO: prevent multi-loading
			
			activities.add(new Activity("Sarah",  1, 1, 1, 0, demoModelDoc, this, 
					"test", "ns", "Talk about Sarah")); 
			activities.add(new Activity("Michael",  1, 1, 1, 0, demoModelDoc, this, 
					"test", "ns", "Talk about Michael")); 
			activities.add(new Activity("Worcester",  1, 1, 1, 0, demoModelDoc, this, 
					"test", "ns", "Talk about Worcester")); 
			activities.add(new Activity("User",  1, 1, 1, 0, demoModelDoc, this, 
					"test", "ns", "Talk about User")); 
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void update(RelationshipManager RM){
		
		
		Set<OWLNamedIndividual> sign = RM.getOntology().getOWLOntology().getIndividualsInSignature();
		
		if(sign.size() < 4){
			RM.getOntology().AddPerson("Sarah", "Worcester", "Siblings");
			RM.getOntology().AddPerson("Michael", "Worcester", "Brother");
			
			sign = RM.getOntology().getOWLOntology().getIndividualsInSignature();
			
			Iterator<OWLNamedIndividual> iter = sign.iterator();
			for(int i=0 ; i<4; i++){
				OWLNamedIndividual current = iter.next();
				ArrayList<OWLNamedIndividual> assoc = new ArrayList<OWLNamedIndividual>();
				assoc.add((OWLNamedIndividual) current);
				
				activities.get((2+i)%4).addAssociation(assoc);
				
				//System.out.println(i + ": " + activities.get((2+i)%4).name + ", " + current.toString());

				iter.remove();
			}
		}
		
		for(Activity act : activities){
			RM.addActivity(act);
		}
	}
	

}
