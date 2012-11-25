package edu.wpi.always.cm.ragclient;

import com.google.gson.*;

import edu.wpi.always.cm.ui.*;
import edu.wpi.always.cm.ui.PluginUtils.InstanceResuseMode;

public class RagKeyboard implements Keyboard, MessageHandler {
	private static final String PLUGIN_NAME = "keyboard";
	static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";

	private volatile String latest;
	private final UIMessageDispatcher dispatcher;

	public RagKeyboard(UIMessageDispatcher dispatcher) {
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
		PluginUtils.startPlugin(dispatcher, PLUGIN_NAME, InstanceResuseMode.Remove, data);
		latest = null;
	}
	

	@Override
	public void handleMessage(JsonObject body) {
		latest = body.get("text").getAsString();
	}

}
