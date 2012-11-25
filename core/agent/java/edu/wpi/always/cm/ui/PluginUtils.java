package edu.wpi.always.cm.ui;

import com.google.gson.*;

import edu.wpi.always.cm.ragclient.*;

public class PluginUtils {
	public enum InstanceResuseMode {
		Remove,
		Reuse,
		Throw
	}

	public static void startPlugin(UIMessageDispatcher dispatcher, String pluginName, InstanceResuseMode mode, JsonObject params){
		Message m = Message.builder("start_plugin").add("name", pluginName).add("instance_reuse_mode", mode.toString()).add("params", params).build();
		dispatcher.send(m);
	}
	public static void closePlugin(UIMessageDispatcher dispatcher){
		Message m = Message.builder("close_plugin").build();
		dispatcher.send(m);
	}
}
