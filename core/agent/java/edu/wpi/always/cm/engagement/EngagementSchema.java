package edu.wpi.always.cm.engagement;

import java.util.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.dialog.*;
import edu.wpi.always.cm.engagement.GeneralEngagementPerception.EngagementState;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.*;

public class EngagementSchema extends SchemaImplBase {

	private final MenuTurnStateMachine stateMachine;
	private final GeneralEngagementPerceptorImpl engagementPerceptor;
	private final EmotiveFacePerceptor facePerceptor;
	private SchemaManager schemaManager;

	public EngagementSchema (BehaviorProposalReceiver behaviorReceiver, GeneralEngagementPerceptorImpl engagementPerceptor,
			EmotiveFacePerceptor facePerceptor, BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor, SchemaManager schemaManager) {
		super(behaviorReceiver, behaviorHistory);
		this.engagementPerceptor = engagementPerceptor;
		this.facePerceptor = facePerceptor;
		this.schemaManager = schemaManager;
		stateMachine = new MenuTurnStateMachine(behaviorHistory, resourceMonitor, menuPerceptor, new RepeatMenuTimeoutHandler());
		stateMachine.setSpecificityMetadata(.9);
	}

	private EngagementState lastState = null;
	@Override
	public void run () {
		BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.05).build();
		GeneralEngagementPerception engPerception = engagementPerceptor.getLatest();
		EmotiveFacePerception facePerception = facePerceptor.getLatest();
		if (engPerception !=null){
			switch(engPerception.getState()){
			case Idle:
				propose(Behavior.newInstance(new IdleBehavior(false)), m);
				break;
			case Attention:
				if(facePerception!=null && facePerception.getLocation()!=null)
					propose(new FaceTrackBehavior(), m);
				break;
			case Initiation:
				propose(Behavior.newInstance(new FaceTrackBehavior(), new SpeechBehavior("Hi")), m);
				break;
			case Engaged:
				if(lastState!=EngagementState.Engaged)
					stateMachine.setAdjacencyPair(new InitialEngagementDialog(schemaManager));
				propose(stateMachine);
				break;
			case Recovering:
				propose(Behavior.newInstance(new FaceTrackBehavior(), new SpeechBehavior("Are you still there"), new MenuBehavior(Arrays.asList("Yes"))), m);
				break;
			}
			lastState = engPerception.getState();
		}
		else
			lastState = null;
	}
}
