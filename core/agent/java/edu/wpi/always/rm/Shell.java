package edu.wpi.always.rm;

import java.beans.Statement;
import java.io.*;
import java.util.StringTokenizer;


public class Shell {
	
	protected BufferedReader input;
	protected String prompt = ">";
	CollaborationManager collab;
	
	//private static Shell shell; //?????

	public static void main (String[] args){
		try{
			make(args, new CollaborationManager()).loop();
		} catch (Throwable e) {}
	} 
	
	public static Shell make (String[] args, CollaborationManager cm) {
		return new Shell(cm); 
	}
	
	protected Shell(CollaborationManager cm){
		input = new BufferedReader(new InputStreamReader( System.in));
		collab = cm;
	}
	
	
	
	public void loop(){
		try{
			while(true) processLine();
		} catch (Throwable e) {}
		// finally { cleanup(); } TODO: cleanup, loggin'
	}
	
	
	protected void processLine () throws Throwable {
		try {
			System.out.print(prompt); 
			String line;
			while(true) {
				line = input.readLine(); 
				if ( line != null ) break;
			}
			processCommand(line); 	
		} catch (Quit e) {throw e; } //TODO: catch?
	}
	
	protected String command;
	
	public void processCommand (String line) throws Throwable {
		//System.out.println("processing");
		line = line.trim();
		StringTokenizer tokenizer = new StringTokenizer(line);
		//System.out.println(line);
		if ( tokenizer.hasMoreTokens() ){
			command = tokenizer.nextToken().toLowerCase();
			
			try{
				new Statement(this, command, new Object[] { line.substring(command.length()).trim() }).execute();
			} catch (Throwable e) {}// TODO deal w throwable?
		}
	}
	
	public void test (String from){
		System.out.println("Ok that worked");
	}
	
	public static class Quit extends Throwable {
		private static final long serialVersionUID = 1L;};
	
	public void report (String reportString){
		//ActivityReport r = new ActivityReport();
		
		// send to CM's RM's Weather plugin... :/
		// CM needs a handle on the PLUGIN so it can communicate.... HMMM. 
		//  TODO: given by task request message?!
	}
	
	public void makeplan (String from){
		System.out.println("Requesting plan.");
		//collab.relationshipModule.doPlan = true;
		collab.relationshipModule.plan();
	}
	
	public void scenarios(String from){
		System.out.println("Requesting plans.");
		
		collab.relationshipModule.scenarioPlans();
	}
	
	
	public void testing(String from){
		collab.relationshipModule.test();
	}
	
	public void testrelevance(String from){
		collab.relationshipModule.relevanceTest(); 
	}
	
	
	// for UM properties files
	public void setProperty(String input){
		//collab.relationshipModule.userModel.setProperty(key, value);
	}
	
	public void getProperty(String input){
		System.out.println("Getting property: " + input);
		System.out.println(collab.relationshipModule.userModel.getProperty(input));
	}

}
