package edu.wpi.always.enroll;

import com.google.gson.*;
import edu.wpi.always.client.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.user.people.*;
import org.joda.time.format.*;

public class EnrollClient implements EnrollUI {

   private static final String PLUGIN_NAME = "enroll";

   private static final String MSG_ENROLL_DISPLAY = "enroll.display";

   private final PeopleManager enroll;

   private final UIMessageDispatcher dispatcher;

   private final DateTimeFormatter formatter = DateTimeFormat
         .forPattern("MMMM dd");

   public EnrollClient (final PeopleManager enroll,
         UIMessageDispatcher dispatcher) {
      this.enroll = enroll;
      this.dispatcher = dispatcher;
   }

   private void show () {
      ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
            InstanceReuseMode.Reuse, null);
   }

   @Override
   public void showAllEntries () {
      show();
      Message m = Message.builder(MSG_ENROLL_DISPLAY).add("type", "people")
            .add("peopleData", getPeopleData()).build();
      dispatcher.send(m);
   }

   @Override
   public void showCurrentEntry (Person person) {
      show();
      String gender = person.getGender() == null ? null : person.getGender().name();
      String zipcode = (person.getLocation() == null) ? null :
         person.getLocation().toString() + ", " + person.getLocation().getZip();
      String spouse = person.getSpouse() == null ? null : person.getSpouse().getName();
      String birthday = (person.getBirthday() == null) ? null :
         person.getBirthday().toString(formatter);
      
      String relationshipName = "";
      if( person.getRelationship() != null )
         relationshipName =person.getRelationship().name();
      
      Message m = Message.builder(MSG_ENROLL_DISPLAY).add("type", "personInfo")
            .add("name", person.getName())
            .add("age", person.getAge())
            .add("gender", gender)
            .add("zipcode", zipcode)
            .add("relationship", relationshipName)
            .add("spouse", spouse)
            .add("skypeAccount", person.getSkypeNumber())
            .add("birthday", birthday)
            .build();
      dispatcher.send(m);
   }

   public JsonElement getPeopleData () {
      JsonArray People = new JsonArray();
      Person[] entries = enroll.getPeople(true);
      for (Person entry : entries)
         People.add(asPersonJson(entry));
      JsonObject peopleData = new JsonObject();
      peopleData.add("entries", People);
      return peopleData;
   }

   private JsonElement asPersonJson (Person src) {
      String gender = (src.getGender() != null) ? src.getGender().toString()
         : null;
      String zipcode = (src.getLocation() != null) ? src.getLocation()
            .toString() + ", " + src.getLocation().getZip() : null;
      String birthday = (src.getBirthday() != null) ? src.getBirthday()
            .toString() : null;
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("name", src.getName());
      jsonObject.addProperty("age", src.getAge());
      jsonObject.addProperty("gender", gender);
      jsonObject.addProperty("relationship", src.getRelationship().name());
      jsonObject.addProperty("zipcode", zipcode);
      jsonObject.addProperty("Spouse", src.getSpouse().getName());
      jsonObject.addProperty("skypeAccount", src.getSkypeNumber());
      jsonObject.addProperty("birthday", birthday);
      return jsonObject;
   }

   @Override
   public void hideEnrollUI () {
      ClientPluginUtils.hidePlugin(dispatcher);
   }
}
