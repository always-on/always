package edu.wpi.always.cm.schemas;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.engagement.*;
import edu.wpi.always.cm.perceptors.*;

public class SimpleGreetingsSchema extends SchemaImplBase implements DialogContentProvider {

	private final FacePerceptor facePerceptor;
	private State state = State.Init;
	private final GeneralEngagementPerceptorImpl engagementPerceptor;
	private OldDialogStateMachine stateMachine;

	public SimpleGreetingsSchema (BehaviorProposalReceiver behaviorReceiver,
			final BehaviorHistory resourceMonitor, FacePerceptor facePerceptor,
			MenuPerceptor menuPerceptor, GeneralEngagementPerceptorImpl engagementPerceptor) {
		super(behaviorReceiver, resourceMonitor);
		this.facePerceptor = facePerceptor;
		this.engagementPerceptor = engagementPerceptor;
		
		BehaviorHistory historyWithFocusRequestAugmenter = behaviorHistoryWithAutomaticInclusionOfFocus();
		stateMachine = new OldDialogStateMachine(historyWithFocusRequestAugmenter, this, menuPerceptor);
		stateMachine.setSpecificityMetadata(.9);

		setNeedsFocusResouce();
	}

	@Override
	public void run () {
		GeneralEngagementPerception engPerception = engagementPerceptor.getLatest();
		if (state == State.Done && (engPerception == null || !engPerception.engaged())) {
			changeStateTo(State.Init);
		}

		propose(stateMachine);
	}

	private ArrayList<String> getUserGreetingMenuItems () {
		return Lists.newArrayList("Hi", "Hello", "Good morning");
	}

	private void changeStateTo (State s) {
		state = s;
		if (state == State.Done) {
			engagementPerceptor.setEngaged(true);
		}
	}

	private enum State {
		Init, WaitingForResponse, Done
	}

	@Override
	public String whatToSay () {
		if (state == State.Init) {
			FacePerception f = facePerceptor.getLatest();
			if (f != null && f.faceLocation() != null) {
				return "Hi";
			}
		}

		return null;
	}

	@Override
	public void doneSaying (String text) {
		changeStateTo(State.WaitingForResponse);
	}

	@Override
	public List<String> userChoices () {
		if (state == State.WaitingForResponse) {
			return getUserGreetingMenuItems();
		}

		return null;
	}

	@Override
	public void userSaid (String text) {
		changeStateTo(State.Done);
	}

	@Override
	public double timeRemaining () {
		double t;

		switch (state) {
		case Init:
			t = 1;
			break;
		case WaitingForResponse:
			t = 0.5;
			break;
		default:
			t = 0;
		}

		return t;
	}
}
