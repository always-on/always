package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.realizer.*;

public class RagSpeechRealizer extends
		SingleRunPrimitiveRealizer<SpeechBehavior> implements
		RagClientProxyObserver {

	private final RagClientProxy proxy;
	private boolean done;

	public RagSpeechRealizer(SpeechBehavior params, RagClientProxy proxy) {
		super(params);
		this.proxy = proxy;
		proxy.addObserver(this);
	}

	@Override
	protected void singleRun() {
		proxy.say(getParams().getText());
	}

	@Override
	public void notifyDone(RagClientProxy sender, String action, String data) {
		if (action.equals("speech") && data.trim().toLowerCase().equals(getParams().getText().trim().toLowerCase())) {
			done = true;
			fireDoneMessage();
			proxy.removeObserver(this);
		}
	}

	@Override
	public void notifyMenuSelected(RagClientProxy ragClientProxy, String text) {
	}

	@Override
	public void shutdown() {
		if(!done)
			proxy.stopSpeech();
	}
	
}
