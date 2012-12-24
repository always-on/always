package edu.wpi.always.client;

public interface UIMessageDispatcher {

	void send (Message message);
	void registerReceiveHandler(String messageType, MessageHandler handler);

}
