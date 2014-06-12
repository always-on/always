package plugins;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import pluginCore.*;

public class AnecdotesSchema extends ScriptbuilderSchema {

	public AnecdotesSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager, Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always, "Anecdotes")),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, dispatcher,
		       Logger.Activity.ANECDOTES);
		 always.getUserModel().setProperty(AnecdotesPlugin.PERFORMED, true);
	}
	
	enum Finished { FINISHED, NOT_FINISHED }
	
	public static void log (Finished finished, String title) {
	   Logger.logActivity(Logger.Activity.ANECDOTES, finished, title);
	}

}