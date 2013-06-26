package edu.wpi.always.enroll.schema;

import edu.wpi.always.client.Keyboard;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.always.enroll.EnrollUI;
import edu.wpi.always.user.UserModel;
import edu.wpi.always.user.people.PeopleManager;
import edu.wpi.always.user.places.PlaceManager;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.BehaviorHistory;
import edu.wpi.disco.rt.behavior.BehaviorProposalReceiver;

public class EnrollSchema extends ActivitySchema{

	private final MenuTurnStateMachine stateMachine;

	public EnrollSchema (BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, Keyboard keyboard, EnrollUI enrollUI, 
			UIMessageDispatcher dispatcher, UserModel model, PlaceManager placeManager, 
			PeopleManager peopleManager) {
		super(behaviorReceiver, behaviorHistory);
		stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor,
				menuPerceptor, new RepeatMenuTimeoutHandler());
		stateMachine.setSpecificityMetadata(.9);
		stateMachine.setAdjacencyPair(new InitialEnroll(new EnrollStateContext(
				keyboard, enrollUI, dispatcher, model, placeManager,
				peopleManager)));
	}

	@Override
	public void run () {
		propose(stateMachine);
	}

}
