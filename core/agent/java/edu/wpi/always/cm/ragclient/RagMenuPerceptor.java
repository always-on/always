package edu.wpi.always.cm.ragclient;

import edu.wpi.always.cm.perceptors.*;

public class RagMenuPerceptor implements MenuPerceptor, RagClientProxyObserver {

	private volatile MenuPerceptionImpl latest;
	
	public RagMenuPerceptor(RagClientProxy proxy) {
		proxy.addObserver(this);
	}
	
	@Override
	public MenuPerception getLatest() {
		return latest;
	}

	@Override
	public void run() {
	}

	@Override
	public void notifyDone(RagClientProxy sender, String action, String data) {
	}

	@Override
	public void notifyMenuSelected(RagClientProxy ragClientProxy, String text) {
		latest = new MenuPerceptionImpl(text);
	}

}
