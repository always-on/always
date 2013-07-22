package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;

public class KeyboardMessageHandler implements Keyboard, MessageHandler{

   private static final String PLUGIN_NAME = "keyboard";
   static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";
   private volatile String latest;
   private boolean overflow;
   private final UIMessageDispatcher dispatcher;

   public KeyboardMessageHandler (UIMessageDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      dispatcher.registerReceiveHandler(MSG_TEXT_UPDATE, this);
   }

   @Override
   public String getInputSoFar () {
      return latest == null ? "" : latest;
   }

   @Override
   public void showKeyboard (String prompt, boolean isNumeric) {
      JsonObject data = new JsonObject();
      data.addProperty("contextMessage", prompt);
      data.addProperty("isNumeric", isNumeric);
      ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
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
      overflow = body.get("isOverflow").getAsBoolean();
   }

   @Override
   public boolean isOverflow () { return overflow; }

   @Override
   public void setOverflow (boolean overflow) { this.overflow = overflow; }
}
