package edu.wpi.always.client;

import com.google.gson.*;
import java.util.Map;

public class Message {

   private final String type;
   private final JsonObject body;

   public Message (String type, JsonObject body) {
      this.type = type;
      this.body = body;
   }

   public Message (String type) {
      this(type, new JsonObject());
   }

   public Message (String type, Map<String, String> properties) {
      this(type, new Gson().toJsonTree(properties).getAsJsonObject());
   }

   public String getType () {
      return type;
   }

   public JsonObject getBody () {
      return body;
   }

   public String getProperty (String property) {
      return body.get(property).getAsString();
   }

   public static Builder builder (String messageType) {
      return new Builder(messageType);
   }

   public static class Builder {

      private final String type;
      private final JsonObject body = new JsonObject();

      private Builder (String messageType) {
         this.type = messageType;
      }

      public Builder add (String name, String value) {
         body.addProperty(name, value);
         return this;
      }

      public Builder add (String name, Number value) {
         body.addProperty(name, value);
         return this;
      }

      public Builder add (String name, Boolean value) {
         body.addProperty(name, value);
         return this;
      }

      public Builder add (String name, JsonElement inner) {
         body.add(name, inner);
         return this;
      }

      public Builder add (Message inner) {
         add(inner.getType(), inner.getBody());
         return this;
      }

      public Message build () {
         return new Message(type, body);
      }
   }
}
