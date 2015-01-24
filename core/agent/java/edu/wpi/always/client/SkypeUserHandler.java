package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.Always;
import edu.wpi.always.user.people.Person;

public class SkypeUserHandler implements MessageHandler {

	public SkypeUserHandler () {}

	public static final String USER_MESSAGE = "videoId";

	@Override
	public void handleMessage (JsonObject body) {
      Person user = Always.THIS.getUserModel().getPeopleManager().getUser();
      if ( user != null ) user.setSkypeNumber(body.get("id").getAsString());
	}

}
