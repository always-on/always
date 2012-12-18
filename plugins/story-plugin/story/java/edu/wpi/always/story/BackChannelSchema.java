package edu.wpi.always.story;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.SpeechPerception.SpeechState;
import edu.wpi.always.cm.perceptors.async.PerceptorBuffer;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.ragclient.AgentFaceExpression;
import edu.wpi.always.cm.ui.Keyboard;

public class BackChannelSchema extends SchemaImplBase {

	AudioFileBehavior speechBehavior = new AudioFileBehavior(BackChannelSchema.class.getResource("/edu/wpi/always/story/mmmhmm.wav"));
	private int speechCounter = 0;
	private static final int SPEECH_PERIOD = 0;
	public SwingCheckBox swingCheckBox;
	private final PerceptorBuffer<SpeechPerception> speechPerceptorBuffer;
	private SpeechState lastState = SpeechState.Silent;
	private long lastSilentAction = 0;
	private static final long MIN_SILENT_ACTION_DELAY = 2000;
	boolean hasDisplayed = false;

	public BackChannelSchema(BehaviorProposalReceiver behaviorReceiver, BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor, MenuPerceptor menuPerceptor, Keyboard keyboard, SpeechPerceptor speechPerceptor) {
		super(behaviorReceiver, behaviorHistory);
		speechPerceptorBuffer = speechPerceptor.newBuffer();

		//
		//
		swingCheckBox = new SwingCheckBox();
		swingCheckBox.createAndShowGUI();
		//
		//
	}

	@Override
	public void run() {
//		if(swingCheckBox.choices[4]){
//			SchemaImplBase.backchanneling = false;
//		}
		SpeechPerception perception;
		while ((perception = speechPerceptorBuffer.next()) != null) {
			onPerception(perception);
		}
	}

	@SuppressWarnings("static-access")
	public void onPerception(SpeechPerception perception) {
		//FaceExpressionBehavior nodBehavior = new FaceExpressionBehavior(AgentFaceExpression.Nod);
		FaceExpressionBehavior nodBehavior = null;

		//
		if(swingCheckBox.choices[0])
			nodBehavior = new FaceExpressionBehavior(AgentFaceExpression.Nod);
		if(swingCheckBox.choices[1])
			nodBehavior = new FaceExpressionBehavior(AgentFaceExpression.Eyebrows_Up);
		if(swingCheckBox.choices[2])
			nodBehavior = new FaceExpressionBehavior(AgentFaceExpression.Blink);
		if(swingCheckBox.choices[3])
			nodBehavior = new FaceExpressionBehavior(AgentFaceExpression.Concern);
		//

		BehaviorMetadata m = new BehaviorMetadataBuilder().specificity(0.9).build();
		SpeechState newState = perception.speakingState();
		if (newState != lastState) {
			if (newState == SpeechState.Silent){
				if(System.currentTimeMillis()-lastSilentAction>MIN_SILENT_ACTION_DELAY){
					++speechCounter;
					if(speechCounter>=SPEECH_PERIOD){
						speechCounter = 0;
						//						List<PrimitiveBehavior> primitives = Lists.newArrayList(speechBehavior, nodBehavior);
						//						List<Constraint> constraints = Lists.newArrayList();
						//						constraints.add(new Constraint(
						//								new SyncRef(SyncPoint.Start, nodBehavior)
						//								,new SyncRef(SyncPoint.Start, speechBehavior),
						//								Type.Before,
						//								10));
						//						@SuppressWarnings("unused") //because voice uhum stuff unused
						//						Behavior b = new Behavior(new CompoundBehaviorWithConstraints(primitives, constraints));
						//propose(b, m);
						propose(nodBehavior, m);
					}
					else{
						propose(nodBehavior, m);
					}
					lastSilentAction = System.currentTimeMillis();
				}
			}

			else{
				//propose(new IdleBehavior(true), m);
				//propose(nodBehavior, m);
				proposeNothing();
			}
			lastState = newState;
		}
	}

}


