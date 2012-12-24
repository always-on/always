package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;

public class ClientMenuPerceptor implements MenuPerceptor, ClientProxyObserver {

	private volatile MenuPerceptionImpl latest;
	
	public ClientMenuPerceptor(ClientProxy proxy) {
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
	public void notifyDone(ClientProxy sender, String action, String data) {
	}

	@Override
	public void notifyMenuSelected(ClientProxy ragClientProxy, String text) {
		latest = new MenuPerceptionImpl(text);
	}

}
