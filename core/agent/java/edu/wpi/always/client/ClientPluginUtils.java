package edu.wpi.always.client;

import com.google.gson.JsonObject;

public abstract class ClientPluginUtils {

   public enum InstanceReuseMode {
      Remove, Reuse, Throw
   }

   private static boolean pluginVisible;
   
   public static boolean isPluginVisible () { return pluginVisible; }
   
   public static void startPlugin (UIMessageDispatcher dispatcher,
         String pluginName, InstanceReuseMode mode, JsonObject params) {
      Message m = Message.builder("start_plugin").add("name", pluginName)
            .add("instance_reuse_mode", mode.toString()).add("params", params)
            .build();
      dispatcher.send(m);
      pluginVisible = true;
   }

   public static void showPlugin (UIMessageDispatcher dispatcher, String pluginName) {
      Message m = Message.builder("show_plugin").add("name", pluginName).build();
      dispatcher.send(m);
      pluginVisible = true;
   }
   
   public static void hidePlugin (UIMessageDispatcher dispatcher) {
      Message m = Message.builder("hide_plugin").build();
      dispatcher.send(m);
      pluginVisible = false;
   }
   
   private static final String KEYBOARD = "keyboard";
   
   public static void showKeyboard (UIMessageDispatcher dispatcher, JsonObject params) {
      ClientPluginUtils.startPlugin(dispatcher, KEYBOARD,
            InstanceReuseMode.Remove, params);
   }
   
   public static void hideKeyboard (UIMessageDispatcher dispatcher) {
      ClientPluginUtils.hidePlugin(dispatcher);
   }

   private ClientPluginUtils () {}
}
