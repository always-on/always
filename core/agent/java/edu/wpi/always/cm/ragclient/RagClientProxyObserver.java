package edu.wpi.always.cm.ragclient;

public interface RagClientProxyObserver {
	void notifyDone(RagClientProxy sender, String action, String additionalData);

	void notifyMenuSelected(RagClientProxy ragClientProxy, String text);
}
