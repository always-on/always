package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.Always;

public abstract class ClientPluginUtils {

   public enum InstanceReuseMode {
      Remove, Reuse, Throw
   }

   private static Boolean pluginVisible; // don't assume state at startup
   
   public static Boolean isPluginVisible () { return pluginVisible; }
   
   // keep track of visible plugin for interruptions
   private static String pluginName; 
   
   public static String getPluginName () { return pluginName; }
   
   public static void startPlugin (UIMessageDispatcher dispatcher,
         String pluginName, InstanceReuseMode mode, JsonObject params) {
      if ( pluginVisible == null || !pluginVisible )
         send(true, dispatcher, pluginName,
               Message.builder("start_plugin").add("name", pluginName)
                .add("instance_reuse_mode", mode.toString()).add("params", params)
                .build());
   }

   public static void showPlugin (UIMessageDispatcher dispatcher, String pluginName) {
      if ( pluginVisible == null || !pluginVisible ) 
         send(true, dispatcher, pluginName,
               Message.builder("show_plugin").add("name", pluginName).build()); 
   }
  
   public static void hidePlugin (UIMessageDispatcher dispatcher) {
      if ( pluginVisible == null || pluginVisible )
         send(false, dispatcher, null,
               Message.builder("hide_plugin").build());
   }
    
   private static void send (boolean visible, UIMessageDispatcher dispatcher, 
         String pluginName, Message m) {
      dispatcher.send(m);
      pluginVisible = visible;
      ClientPluginUtils.pluginName = pluginName;
   }
   
  public static final String KEYBOARD = "keyboard";
   
   // note forcing messages for KB visibility to be sent
   
   public static void showKeyboard (UIMessageDispatcher dispatcher, JsonObject params) {
      pluginVisible = null; // force send
      ClientPluginUtils.startPlugin(dispatcher, KEYBOARD, InstanceReuseMode.Remove, params);
      pluginVisible = null; // force next send
   }
   
   public static void hideKeyboard (UIMessageDispatcher dispatcher) {
      pluginVisible = null;  // force send
      ClientPluginUtils.hidePlugin(dispatcher);
      pluginVisible = null; // force next send
   }

   private ClientPluginUtils () {}
}
