package edu.wpi.always.cm.ragclient;

import com.google.gson.*;

public interface MessageHandler {
	void handleMessage(JsonObject body);
}
