package edu.wpi.always.client;

import com.google.gson.JsonObject;

public class SkypeUserHandler implements MessageHandler {

	public SkypeUserHandler () {}

	public static String USER_ID;
	
	public static final String USER_MESSAGE = "videoId";

	@Override
	public void handleMessage (JsonObject body) {
		USER_ID = body.get("id").getAsString();
		//System.out.println("set USER_ID");
	}

}
