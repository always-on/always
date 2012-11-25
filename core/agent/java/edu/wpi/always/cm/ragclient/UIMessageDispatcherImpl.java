package edu.wpi.always.cm.ragclient;

import java.util.*;
import java.util.concurrent.*;

public class UIMessageDispatcherImpl implements UIMessageDispatcher, TcpConnectionObserver {
	private final UIMessagingJson json = new UIMessagingJson();
	private final RemoteConnection conn;
	private final HashMap<String, MessageHandler> handlers = new HashMap<String, MessageHandler>();
	private final ExecutorService receivedMessageNotifierService = Executors.newFixedThreadPool(1);

	public UIMessageDispatcherImpl (RemoteConnection connection) {
		conn = connection;
		conn.addObserver(this);
	}

	@Override
	public void notifyMessageReceive (RemoteConnection sender, String text) {
		handleMessage(text);
	}

	@Override
	public void send (Message message) {
		conn.beginSend(json.generate(message));
	}

	@Override
	public void registerReceiveHandler (String messageType, MessageHandler handler) {
		if (handlers.containsKey(messageType))
			throw new IllegalStateException("A handler for <" + messageType + "> already exists");

		handlers.put(messageType, handler);
	}

	public void handleMessage (String text) {
		for (String s : JsonBreakDown.stringsOfIndividualClsses(text)) {
			final Message msg = json.parse(s);

			if(msg == null)
				continue;
			
			if (!handlers.containsKey(msg.getType()))
				throw new InvalidMessageTypeException(msg.getType());

			receivedMessageNotifierService.execute(new Runnable() {
				public void run() {
					handlers.get(msg.getType()).handleMessage(msg.getBody());
				}
			});
		}
	}

}
