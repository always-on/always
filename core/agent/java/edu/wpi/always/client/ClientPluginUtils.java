package edu.wpi.always.client;

import com.google.gson.JsonObject;

public abstract class ClientPluginUtils {

   public enum InstanceReuseMode {
      Remove, Reuse, Throw
   }

   public static void startPlugin (UIMessageDispatcher dispatcher,
         String pluginName, InstanceReuseMode mode, JsonObject params) {
      Message m = Message.builder("start_plugin").add("name", pluginName)
            .add("instance_reuse_mode", mode.toString()).add("params", params)
            .build();
      dispatcher.send(m);
   }

   public static void closePlugin (UIMessageDispatcher dispatcher) {
      Message m = Message.builder("close_plugin").build();
      dispatcher.send(m);
   }
   
   private ClientPluginUtils () {}
}
