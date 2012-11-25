package edu.wpi.always.cm.ragclient;

import com.google.gson.*;

public class UIMessagingJson {
	private static final String MSG_BODY = "msg_body";
	private static final String MSG_TYPE = "msg_type";
	private String saved;

	Message parse(String jsonStr) {
		Gson g = new Gson();
		JsonObject o;
		try {
			if(saved != null) {
				jsonStr = saved + jsonStr;
				saved = null;
			}
			o = g.fromJson(jsonStr, JsonElement.class).getAsJsonObject();
		} catch (com.google.gson.JsonSyntaxException ex) {
			System.out.println("json syntax error for <" + jsonStr + ">");
			if (ex.getCause() instanceof java.io.EOFException || ex.getCause() instanceof com.google.gson.stream.MalformedJsonException) {
				saved = jsonStr;
				return null;
			} else {
				throw ex;
			}
		}

		JsonElement t = o.get(MSG_TYPE);

		if (t == null)
			throw new JsonMessageFormatException("Json message missing a type");

		if (t.isJsonPrimitive()) {
			JsonElement b = o.get(MSG_BODY);
			JsonObject body = (b != null && b.isJsonObject()) ? b.getAsJsonObject() : null;
			return new Message(t.getAsString(),
					body);
		}

		throw new JsonMessageFormatException("Message type should be a string");
	}

	String generate(Message msg) {
		JsonObject o = new JsonObject();
		o.addProperty(MSG_TYPE, msg.getType());
		o.add(MSG_BODY, msg.getBody());

		return new Gson().toJson(o);
	}
}
