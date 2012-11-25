package edu.wpi.always.cm.primitives.console;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class ConsoleSpeechRealizer extends
		SingleRunPrimitiveRealizer<SpeechBehavior> {

	public ConsoleSpeechRealizer(SpeechBehavior params) {
		super(params);
	}

	@Override
	protected void singleRun() {
		System.out.println("Saying: " + getParams().getText());
		fireDoneMessage();
	}

}
