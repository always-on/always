package plugins;

import edu.wpi.always.Always;
import edu.wpi.always.client.*;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;
import pluginCore.*;

public class ExerciseSchema extends ScriptbuilderSchema {

	public ExerciseSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager,Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	               keyboard, dispatcher, placeManager, peopleManager, always)),
		       behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
	}

}