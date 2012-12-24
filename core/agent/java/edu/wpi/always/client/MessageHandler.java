package edu.wpi.always.client;

import com.google.gson.JsonObject;

public interface MessageHandler {

   void handleMessage (JsonObject body);
}
