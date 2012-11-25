package edu.wpi.always.cm.ragclient;

public interface UIMessageDispatcher {

	void send (Message message);
	void registerReceiveHandler(String messageType, MessageHandler handler);

}
