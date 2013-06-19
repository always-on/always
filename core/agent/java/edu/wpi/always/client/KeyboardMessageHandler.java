package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.disco.Agent;
import edu.wpi.disco.Interaction;
import edu.wpi.disco.lang.Say;
import edu.wpi.disco.lang.Utterance;

public class KeyboardMessageHandler implements Keyboard, MessageHandler {

	private static final String PLUGIN_NAME = "keyboard";
	static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";
	private static final String KEYBOARD_EXCEED = "keyboard.exceed";
	static final String NUM_PLUGIN_NAME = "numkeyboard";
	private volatile String latest;
	private final UIMessageDispatcher dispatcher;

	public KeyboardMessageHandler (UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		dispatcher.registerReceiveHandler(MSG_TEXT_UPDATE, this);
	    dispatcher.registerReceiveHandler(KEYBOARD_EXCEED,
	              new MessageHandler() {
	    	
	    	@Override
	    	public void handleMessage (JsonObject body) {
	    		String event = body.get("event").getAsString();
	    		if(event.equals("exceed")){
	    			String prompt = body.get("prompt").getAsString();
	    			//TODO: agent say the prompt
	    		}
	    	}
	    });
	}

	@Override
	public String getInputSoFar () {
		if ( latest != null )
			return latest;
		return "";
	}

	@Override
	public void showKeyboard (String prompt) {
		JsonObject data = new JsonObject();
		data.addProperty("contextMessage", prompt);
		ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
				InstanceReuseMode.Remove, data);
		latest = null;
	}

	@Override
	public void ShowNumKeyboard(String prompt) {
		JsonObject data = new JsonObject();
		data.addProperty("contextMessage", prompt);
		ClientPluginUtils.startPlugin(dispatcher, NUM_PLUGIN_NAME,
				InstanceReuseMode.Remove, data);
		latest = null;
	}

	@Override
	public void hideKeyboard () {
		ClientPluginUtils.closePlugin(dispatcher);
	}

	@Override
	public void handleMessage (JsonObject body) {
		latest = body.get("text").getAsString();
	}
}
