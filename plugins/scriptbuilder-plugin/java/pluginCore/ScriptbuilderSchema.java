package pluginCore;

import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.FaceExpressionBehavior;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class ScriptbuilderSchema extends ActivityStateMachineSchema {

	public ScriptbuilderSchema(BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard,
			UIMessageDispatcher dispatcher, PlaceManager placeManager,
			PeopleManager peopleManager,Always always) {
		 super(new ScriptbuilderCoreScript(new RAGStateContext(
	                keyboard, dispatcher, placeManager, peopleManager,always)),
		            behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
	}

}