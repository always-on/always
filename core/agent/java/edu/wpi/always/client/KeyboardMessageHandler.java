package edu.wpi.always.client;

import com.google.gson.*;

import edu.wpi.always.client.ClientPluginUtils.InstanceResuseMode;
import edu.wpi.always.cm.ui.*;

public class KeyboardMessageHandler implements Keyboard, MessageHandler {
	private static final String PLUGIN_NAME = "keyboard";
	static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";

	private volatile String latest;
	private final UIMessageDispatcher dispatcher;

	public KeyboardMessageHandler(UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		dispatcher.registerReceiveHandler(MSG_TEXT_UPDATE, this);
	}

	@Override
	public String getInputSoFar() {
		if(latest != null)
			return latest;
		return "";
	}

	@Override
	public void showKeyboard(String prompt) {
		JsonObject data = new JsonObject();
		data.addProperty("contextMessage", prompt);
		ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME, InstanceResuseMode.Remove, data);
		latest = null;
	}
	

	@Override
	public void handleMessage(JsonObject body) {
		latest = body.get("text").getAsString();
	}

}
