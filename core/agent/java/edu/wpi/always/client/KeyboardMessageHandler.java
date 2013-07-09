package edu.wpi.always.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;

public class KeyboardMessageHandler implements Keyboard, MessageHandler{

	private static final String PLUGIN_NAME = "keyboard";
	static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";
	private static final String KEYBOARD_EXCEED = "keyboard.exceed";
	private volatile String latest;
	private static boolean isOverflow;
	private final UIMessageDispatcher dispatcher;
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

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
					System.out.println(prompt);
					//TODO: Let the Agent speak out the prompt
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
	public void showKeyboard (String prompt, boolean isNumeric) {
		if(!isOverflow) {
			JsonObject data = new JsonObject();
			data.addProperty("contextMessage", prompt);
			data.addProperty("isNumeric", isNumeric);
			ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
					InstanceReuseMode.Remove, data);
			latest = null;
		}
	}

	@Override
	public void hideKeyboard () {
		ClientPluginUtils.closePlugin(dispatcher);
	}

	@Override
	public void handleMessage (JsonObject body) {
		latest = body.get("text").getAsString();
		isOverflow = body.get("isOverflow").getAsBoolean();
		if(isOverflow) {
			sendIsOverflowListener(); 
		}
	}

	private void sendIsOverflowListener() {
		propertyChangeSupport.firePropertyChange("isOverflow", null, null);
	}
	
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      propertyChangeSupport.addPropertyChangeListener(listener);
  }
}
