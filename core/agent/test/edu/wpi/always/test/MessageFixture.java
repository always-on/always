package edu.wpi.always.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import edu.wpi.always.client.Message;

public class MessageFixture {

	@Test
	public void CtorWithMap () {
		HashMap<String, String> body = new HashMap<String, String>();

		body.put("first", "interesting");
		body.put("second", "good");
		body.put("last", "okay");

		Message m = new Message("haha", body);

		assertEquals("haha", m.getType());
		assertEquals("interesting", m.getBody().get("first").getAsString());
		assertEquals("good", m.getBody().get("second").getAsString());
		assertEquals("okay", m.getBody().get("last").getAsString());
	}
}
