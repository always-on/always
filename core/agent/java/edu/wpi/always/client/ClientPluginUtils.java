package edu.wpi.always.client;

import com.google.gson.JsonObject;

public abstract class ClientPluginUtils {

   public enum InstanceReuseMode {
      Remove, Reuse, Throw
   }

   private static Boolean pluginVisible; // don't assume state at startup
   
   public static Boolean isPluginVisible () { return pluginVisible; }
   
   public static void startPlugin (UIMessageDispatcher dispatcher,
         String pluginName, InstanceReuseMode mode, JsonObject params) {
      if ( pluginVisible == null || !pluginVisible ) {
         Message m = Message.builder("start_plugin").add("name", pluginName)
               .add("instance_reuse_mode", mode.toString()).add("params", params)
               .build();
         dispatcher.send(m);
         pluginVisible = true;
      }
   }

   public static void showPlugin (UIMessageDispatcher dispatcher, String pluginName) {
      if ( pluginVisible == null || !pluginVisible ) {
         Message m = Message.builder("show_plugin").add("name", pluginName).build();
         dispatcher.send(m);
         pluginVisible = true;
      }
   }
   
   public static void hidePlugin (UIMessageDispatcher dispatcher) {
      if ( pluginVisible == null || pluginVisible ) {
         Message m = Message.builder("hide_plugin").build();
         dispatcher.send(m);
         pluginVisible = false;
      }
   }
   
   private static final String KEYBOARD = "keyboard";
   
   // note forcing messages for KB visibility to be sent
   
   public static void showKeyboard (UIMessageDispatcher dispatcher, JsonObject params) {
      pluginVisible = null;
      ClientPluginUtils.startPlugin(dispatcher, KEYBOARD,
            InstanceReuseMode.Remove, params);
      pluginVisible = null;
   }
   
   public static void hideKeyboard (UIMessageDispatcher dispatcher) {
      pluginVisible = null;
      ClientPluginUtils.hidePlugin(dispatcher);
   }

   private ClientPluginUtils () {}
}
