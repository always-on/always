package edu.wpi.always.client;

import com.google.gson.*;

public interface MessageHandler {
	void handleMessage(JsonObject body);
}
