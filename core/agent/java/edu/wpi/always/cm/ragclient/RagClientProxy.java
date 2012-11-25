package edu.wpi.always.cm.ragclient;

import java.util.*;
import java.util.concurrent.*;

import com.google.common.collect.*;
import com.google.gson.*;

public class RagClientProxy {
	private final List<RagClientProxyObserver> observers;
	private final UIMessageDispatcher dispatcher;

	public RagClientProxy (UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		observers = new CopyOnWriteArrayList<RagClientProxyObserver>();

		registerOnDispatcher();
	}

	private void registerOnDispatcher () {
		this.dispatcher.registerReceiveHandler("done", new MessageHandler() {

			@Override
			public void handleMessage (JsonObject body) {
				String data = "";
				JsonElement dataElem = body.get("data");
				if(dataElem != null && dataElem.isJsonPrimitive())
					data = dataElem.getAsString();
				
				fireDoneMessage(body.get("action").getAsString(), data);
			}
		});

		this.dispatcher.registerReceiveHandler("menu_selected", new MessageHandler() {

			@Override
			public void handleMessage (JsonObject body) {
				fireMenuSelectedMessage(body.get("text").getAsString());
			}
		});
	}

	private void enqueue (Message message) {
		dispatcher.send(message);
	}

	private void enqueue (String messageType, HashMap<String, String> body) {
		enqueue(new Message(messageType, body));
	}

	public void say (String text) {
		HashMap<String, String> p = Maps.newHashMap();
		p.put("text", text);

		enqueue("speech", p);
	}

	public void gaze (AgentTurn dir) {
		HashMap<String, String> p = Maps.newHashMap();
		p.put("dir", dir.toString());

		enqueue("gaze", p);
	}

	public void express(AgentFaceExpression expression) {
		HashMap<String, String> p = Maps.newHashMap();
		p.put("expression", expression.toString());

		enqueue("express", p);
	}

	public void addObserver (RagClientProxyObserver observer) {
		if (observer == null)
			throw new IllegalArgumentException("observer is null");

		observers.add(observer);
	}

	public void removeObserver (RagClientProxyObserver observer) {
		observers.remove(observer);
	}

	public void fireDoneMessage (String action, String data) {
		for (RagClientProxyObserver o : observers) {
			o.notifyDone(this, action, data);
		}
	}

	private void fireMenuSelectedMessage (String text) {
		for (RagClientProxyObserver o : observers) {
			o.notifyMenuSelected(this, text);
		}
	}

	public void showMenu (List<String> items, boolean twoColumn) {
		JsonArray menus = new JsonArray();

		for (String s : items)
			menus.add(new JsonPrimitive(s));

		JsonObject body = new JsonObject();
		body.add("menus", menus);
		body.addProperty("twoColumn", twoColumn);

		enqueue(new Message("show_menu", body));
	}

	public void stopSpeech() {
		enqueue(new Message("stop_speech", new JsonObject()));
	}

	public void idle(boolean enable) {
		HashMap<String, String> p = Maps.newHashMap();
		p.put("enabled", enable?"true":"false");
		enqueue("idle", p);
	}

}
