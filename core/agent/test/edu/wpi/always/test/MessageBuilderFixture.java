package edu.wpi.always.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.JsonObject;

import edu.wpi.always.client.Message;

public class MessageBuilderFixture {
	@Test
	public void test () {
		Message m = Message.builder("gaze")
						.add("x", 10)
						.add("y", 20)
						.build();

		JsonObject expected = new JsonObject();
		expected.addProperty("x", 10);
		expected.addProperty("y", 20);

		assertEquals("gaze", m.getType());
		assertEquals(expected, m.getBody());
	}

	@Test
	public void withStringAndBooleanProperties () {
		Message m = Message.builder("good")
						.add("Hello", "World")
						.add("Hi", "Cosmos")
						.add("Am I right?", false)
						.build();
		
		JsonObject expected = new JsonObject();
		expected.addProperty("Hello", "World");
		expected.addProperty("Hi", "Cosmos");
		expected.addProperty("Am I right?", false);
		
		assertEquals("good", m.getType());
		assertEquals(expected, m.getBody());
	}
	
	@Test
	public void withJsonObjectProperty() {
		JsonObject inner = new JsonObject();
		inner.addProperty("deep", "content");
		
		Message m = Message.builder("good")
						.add("i", inner)
						.build();
		
		JsonObject expected = new JsonObject();
		expected.add("i", inner);
		
		assertEquals("good", m.getType());
		assertEquals(expected, m.getBody());
	}
	
	@Test
	public void withInnerMessage() {
		Message inner = Message.builder("good feature?")
							.add("You?", "Yes, me!")
							.build();
		
		Message m = Message.builder("outer")
						.add(inner)
						.build();
		
		assertEquals("outer", m.getType());
		assertEquals(inner.getBody(), m.getBody().get("good feature?"));
	}
}
