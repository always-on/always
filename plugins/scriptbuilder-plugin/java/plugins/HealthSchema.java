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
import plugins.AnecdotesSchema.Finished;

public class HealthSchema extends ScriptbuilderSchema {

	public HealthSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager, Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always, "Education")),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, dispatcher,
		       Logger.Activity.HEALTH);
		 always.getUserModel().setProperty(HealthPlugin.PERFORMED, true);
	}

	// TODO define enums as appropriate
	
	public static void log (Object... args) {
      Logger.logActivity(Logger.Activity.HEALTH, args);
   }
}