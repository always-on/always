package pluginCore;

<<<<<<< HEAD
import edu.wpi.always.*;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.primitives.FaceExpressionBehavior;
import edu.wpi.always.cm.schemas.*;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
=======
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
>>>>>>> upstream/develop
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class ScriptbuilderSchema extends ActivityStateMachineSchema {

<<<<<<< HEAD
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
=======
   public ScriptbuilderSchema (AdjacencyPair init,
         BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor) {
		 super(init, behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
	}
   
   @Override
   public void run () {
      // TODO: need to call cancel() when nothing left in script!
      super.run();
   }
}
>>>>>>> upstream/develop
