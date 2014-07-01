package edu.wpi.always.client;

import com.google.gson.JsonObject;
import edu.wpi.always.cm.schemas.SessionSchema;
import edu.wpi.always.user.people.*;

public class SkypeInterruptHandler implements MessageHandler {

   private final PeopleManager people;
   
   public SkypeInterruptHandler (PeopleManager people) { this.people = people; }
   
   // CALLER_NAME used in _SkypeInterruption script
   // see definition in edu.wpi.always.resources.Activities.d4g.xml
   public static String CALLER_ID, CALLER_NAME;
   
   // public method for testing
   // e.g., in command line of Session window, type:
   //   eval edu.wpi.always.client.SkypeInterruptHandler.interrupt("Bob")
   //
   public static boolean interrupt (String caller) {
      CALLER_NAME = caller;
      return SessionSchema.interrupt("_SkypeInterruption");
   }
   
   public static final String SKYPE_MESSAGE = "videoCall";
   
   @Override
   public void handleMessage (JsonObject body) {
      // Note: you can set other static variables here
      // and similarly refer to them in SkypeSchema
      CALLER_ID = body.get("id").getAsString();
      Person caller = people.getPersonFromSkype(CALLER_ID);
      interrupt(caller == null ? "someone you know" : caller.getName());
   }
   
}
  