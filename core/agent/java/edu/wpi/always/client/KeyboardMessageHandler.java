package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;

public class KeyboardMessageHandler implements Keyboard, MessageHandler{

   private static final String PLUGIN_NAME = "keyboard";
   static final String MSG_TEXT_UPDATE = "keyboard.textUpdate";
   private static final String KEYBOARD_EXCEED = "keyboard.exceed";
   private volatile String latest;
   public static boolean isOverflow;
   private final UIMessageDispatcher dispatcher;

   public KeyboardMessageHandler (UIMessageDispatcher dispatcher) {
      this.dispatcher = dispatcher;
      dispatcher.registerReceiveHandler(MSG_TEXT_UPDATE, this);
      dispatcher.registerReceiveHandler(KEYBOARD_EXCEED,
            new MessageHandler() {

         @Override
         public void handleMessage (JsonObject body) {
            String event = body.get("event").getAsString();
            if ( event.equals("exceed" )) {
               String prompt = body.get("prompt").getAsString();
               System.out.println(prompt);
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
      isOverflow = body.get("isOverflow").getAsBoolean();
   }
}
