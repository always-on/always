package pluginCore;

import org.w3c.dom.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.*;

import DialogueRuntime.*;
import alwaysAvailableCore.*;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.client.Message.Builder;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;

public class RAGStateContext {
	public static int menuChoice = -1;
	private final Keyboard keyboard;
	private final UIMessageDispatcher dispatcher;
	private final PlaceManager placeManager;
	private final PeopleManager peopleManager;
	//DSM Variables
	public static ArrayList<Message> messageQue = new ArrayList<Message>();
	public static boolean outputOnly = false;
	private static AAECAServer ecaServer;
	private static DialogueStateMachine DSM;
	private static AAECADialogueSession Session;
	private static DocumentBuilderFactory documentBuilderFactory;
	private static DocumentBuilder documentBuilder;
	private static boolean firstRun = true;
	
	private static UserModel userModel;
	
	public RAGStateContext(Keyboard keyboard, UIMessageDispatcher dispatcher,
			PlaceManager placeManager, PeopleManager peopleManager,Always always) {
		this.keyboard = keyboard;
		this.dispatcher = dispatcher;
		this.placeManager = placeManager;
		this.peopleManager = peopleManager;
		this.userModel = always.getUserModel();
	}

	public Keyboard getKeyboard() {
		return keyboard;
	}
	public UIMessageDispatcher getDispatcher() {
		return dispatcher;
	}
	public PlaceManager getPlaceManager() {
		return placeManager;
	}
	public PeopleManager getPeopleManager() {
		return peopleManager;
	}
	public static void createDSM() {
		//FIXME CLEAN THIS!
		
		String topScript = "top";
		int user_id = 1;
		try {
			ecaServer = new AAECAServer(null, topScript, user_id,userModel);
			Session = ecaServer.getSession();
			DSM = Session.getDSM();
		} catch (Exception e) {
			System.err.println("ex: " + e);
		}
	}

	// Support functions to poke the DSM
	private static void populateDialogue() {
		if (firstRun){
			try {
				firstRun = false;
				documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
				createDSM();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String output = "";
		try {
			if(menuChoice != -1){
				DSM.doAction(menuChoice);
			}
			//Skip through Action_Only states
			if(DSM.getStateType() == DialogueState.ACTION_ONLY){
				while (DSM.getStateType() == DialogueState.ACTION_ONLY) {
					DSM.doAction(0);
				}
			}
			//Set state type
			if(DSM.getStateType() == DialogueState.OUTPUT_ONLY)
				outputOnly = true;
			else
				outputOnly = false;
			//getDSM output
			output = DSM.getOutput().getOutput();
			output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<rag>" + output + "</rag>";
			Document doc = documentBuilder.parse(new ByteArrayInputStream(output.getBytes("UTF-8")));
			NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
			Builder b;
			for(int i = 0; i < nodeList.getLength(); i++){
				Node tempNode = nodeList.item(i);
				switch(tempNode.getNodeName().toUpperCase()){
					case "SPEECH":
						b = Message.builder(tempNode.getNodeName());
						b.add("text",tempNode.getTextContent());
						messageQue.add(b.build());
						break;
					case "PAGE":
						break;
					case "POSTURE":
						break;
					case "CAMERA":
						break;
					case "FACE":
						b = Message.builder("express");
//						b.add("expression",tempNode.getAttributes().getNamedItem("EXPR").getNodeValue());
						b.add("expression","Concern");
						//messageQue.add(b.build());
						break;
					case "EYEBROWS":
						System.out.println("EYEBROWS Called");
						break;
					case "SHOW_MENU":
						break;
					case "GESTURE":
						break;
					case "DELAY":
						break;
					case "GAZE":
						break;
					default:
						System.out.println("Default triggered with:" + tempNode.getNodeName().toUpperCase());
						/*
						if(tempNode.hasAttributes()){
							NamedNodeMap nodeMap = tempNode.getAttributes();
							for(int j = 0; j < nodeMap.getLength(); j++){
								Node node = nodeMap.item(j);
								b.add(node.getNodeName(), node.getNodeValue());
							}
						}
						messageQue.add(b.build());*/
						break;
				}
			}
			/*if(DSM.getStateType() == DialogueState.OUTPUT_ONLY){
				menuChoice = 0;
				populateDialogue();
			}*/
			
		} catch(Exception e){
			System.out.println("Exception caught:" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static Message getNextMessage(){
		System.out.println("in get next message");
		if(messageQue.size() == 0){
			populateDialogue();
		}
		if(messageQue.size() > 0)
			return messageQue.remove(0);
		else
			return null;
	}
	
	public static OutputText[] getMenuPrompts(){
		try {
			return DSM.getMenuPrompts();
		} catch (Exception e) {
			return null;
		}
	}
}
