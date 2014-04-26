package pluginCore;

import org.w3c.dom.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import javax.xml.parsers.*;
import DialogueRuntime.*;
import alwaysAvailableCore.*;
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.client.Message.Builder;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.cetask.Utils;
import edu.wpi.disco.rt.menu.AdjacencyPair;

public class RAGStateContext extends AdjacencyPair.Context {
	public static int menuChoice = -1;
	private final Keyboard keyboard;
	private static UIMessageDispatcher dispatcher;
	private final PlaceManager placeManager;
	private final PeopleManager peopleManager;
	// DSM Variables
	public static ArrayList<Message> messageQue = new ArrayList<Message>();
	public static boolean outputOnly = false;
	private static AAECAServer ecaServer;
	private static DialogueStateMachine DSM;
	private static AAECADialogueSession Session;
	private static DocumentBuilderFactory documentBuilderFactory;
	private static DocumentBuilder documentBuilder;
	public static boolean firstRun = true;

	private static UserModel userModel;

	public static boolean isDone = false;

	public static String module;

	public static ShoreFacePerceptor shore;

	public RAGStateContext(Keyboard keyboard, UIMessageDispatcher dispatcher,
			PlaceManager placeManager, PeopleManager peopleManager,
			Always always, ShoreFacePerceptor shore, String module) {
		this.firstRun = true;
		this.keyboard = keyboard;
		this.dispatcher = dispatcher;
		this.placeManager = placeManager;
		this.peopleManager = peopleManager;
		this.userModel = always.getUserModel();
		this.module = module;
		this.shore = shore;
		if (firstRun) {
			try {
				documentBuilderFactory = DocumentBuilderFactory.newInstance();
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				Utils.rethrow(e);
			}
		}
	}

	public void resetPluginStatus(){
		firstRun = true;
		isDone = false;
     	 menuChoice = -1;
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
		String topScript = "top";
		switch (module) {
			case "Anecdotes":
				topScript = "StoryTop";
				break;
			case "Exercise":
				topScript = "ExerciseTop";
				break;
			case "Nutrition":
				topScript = "NutritionTop";
				break;
			case "Education":
				topScript = "EducationTop";
				break;
			case "Storytelling":
				topScript = "StorytellingTop";
				break;
		}
		ecaServer = new AAECAServer(null, topScript, 1, userModel);
		Session = ecaServer.getSession();
		DSM = Session.getDSM();
	}

	// Support functions to poke the DSM
	private static boolean populateDialogue() {
		if (firstRun) {
			firstRun = false;
			createDSM();
		}
		String output = "";
		try {
			if (menuChoice != -1) {
				DSM.doAction(menuChoice);
			}
			// Skip through Action_Only states
			if (DSM.getStateType() == DialogueState.ACTION_ONLY) {
				while (DSM.getStateType() == DialogueState.ACTION_ONLY) {
					DSM.doAction(0);
				}
			}
			// Set state type
			if (DSM.getStateType() == DialogueState.OUTPUT_ONLY)
				outputOnly = true;
			else
				outputOnly = false;
			// getDSM output
			output = DSM.getOutput().getOutput();
			// System.out.println("outputraw:" + output);
			output = output.replace("<speech>", "");
			output = output.replace("</speech>", "");
			Builder b;
			String speechText = "";
			output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<rag>"
					+ output + "</rag>";
			Document doc = documentBuilder.parse(new ByteArrayInputStream(
					output.getBytes("UTF-8")));
			NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
			// Page Test
			// Message test = Message.builder("page").add("url",
			// "file:///C:/AlwaysAvailable/WWW/pillbox.jpg").build();
			// dispatcher.send(test);
			// End of Test
			Message msg;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node tempNode = nodeList.item(i);
				// System.out.println(tempNode.getNodeName());
				switch (tempNode.getNodeName().toUpperCase()) {
				// case "SPEECH":
					case "#TEXT":
						speechText += tempNode.getTextContent() + " ";
						break;
					case "TURNOFFSHORE":
						if (shore != null)
							shore.stop();
						break;
					case "RECORD":
						ClientPluginUtils.startPlugin(dispatcher, "story",
								InstanceReuseMode.Reuse, null);
						msg = Message.builder("story.startRecording").build();
						dispatcher.send(msg);
						break;
					case "STOPRECORD":
						msg = Message.builder("story.stopRecording").build();
						dispatcher.send(msg);
						ClientPluginUtils.hidePlugin(dispatcher);
						break;
					case "TURNONSHORE":
						if (shore != null)
							shore.start();
						break;
					case "PAGE":
						msg = Message
								.builder("page")
								.add("url",
										tempNode.getAttributes()
												.getNamedItem("URL").getNodeValue())
								.build();
						dispatcher.send(msg);
						break;
					case "POSTURE":
						speechText += "<POSTURE/> ";
						break;
					case "CAMERA":
						speechText += "<CAMERA "
								+ tempNode.getAttributes().getNamedItem("zoom")
								+ "/> ";
						break;
					case "FACE":
						speechText += "<FACE "
								+ tempNode.getAttributes().getNamedItem("expr")
								+ "/> ";
						break;
					case "EYEBROWS":
						speechText += "<EYEBROWS "
								+ tempNode.getAttributes().getNamedItem("DIR")
								+ "/> ";
						break;
					case "SHOW_MENU":
						System.out.println("ERROR: GOT SHOW_MENU!");
						break;
					case "GESTURE":
						speechText += "<GESTURE "
								+ tempNode.getAttributes().getNamedItem("hand")
								+ " "
								+ tempNode.getAttributes().getNamedItem("cmd")
								+ "/> ";
						break;
					case "DELAY":
						speechText += "<DELAY "
								+ tempNode.getAttributes().getNamedItem("ms")
								+ "/> ";
						break;
					case "GAZE":
						// TODO: SUPPORT BOTH VERSIONS OF GAZE
						// speechText += "<GAZE " +
						// tempNode.getAttributes().getNamedItem("dir") + "/> ";
						break;
					default:
						System.out.println("Default triggered with:"
								+ tempNode.getNodeName().toUpperCase());
						break;
				}

			}
			b = Message.builder("speech");
			b.add("text", speechText);
			messageQue.add(b.build());

		} catch (Exception e) {
			if (DSM.stack.empty()) {
				System.out.println("Done with script!");
				isDone = true;
				return false;
			} else {
				Utils.rethrow(e);
			}
		}
		return true;
	}

	public static Message getNextMessage() {
		System.out.println("in get next message");
		if (messageQue.size() == 0) {
			if (!populateDialogue()) {
				Builder b;
				b = Message.builder("speech");
				b.add("text", "");
				return b.build();
			}
		}
		if (messageQue.size() > 0)
			return messageQue.remove(0);
		else
			return null;
	}

	public static OutputText[] getMenuPrompts() {
		try {
			return DSM.getMenuPrompts();
		} catch (Exception e) {
			return null;
		}
	}
}
