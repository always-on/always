package edu.wpi.always.test;

import static org.junit.Assert.assertEquals;
import edu.wpi.always.client.JsonBreakDown;
import org.junit.Test;
import java.util.*;

public class JsonBreakDownFixture {
	@Test
	public void StringContainingOneObject () {
		List<String> l = JsonBreakDown
				.stringsOfIndividualClsses("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}");

		assertEquals(1, l.size());
		assertEquals("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}", l.get(0));
	}

	@Test
	public void StringContainingLessThanOneObject () {
		// note: the missing } at the end of string
		String original = "{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}";
		List<String> l = JsonBreakDown
				.stringsOfIndividualClsses(original);

		assertEquals(1, l.size());
		assertEquals(original, l.get(0));
	}

	@Test
	public void StringWithNoJson () {
		List<String> l = JsonBreakDown
				.stringsOfIndividualClsses("Hello, how are you?");

		assertEquals(1, l.size());
		assertEquals("Hello, how are you?", l.get(0));
	}

	@Test
	public void StringContainingTwoObjects () {
		String[] thingsBetweenThem = new String[] { "", "  ", "\n", "\r\n" };

		for (String d : thingsBetweenThem) {
			List<String> l = JsonBreakDown
					.stringsOfIndividualClsses(
								"{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}" + d +
										"{\"msg_type\":\"speech\",\"msg_body\":{\"text\":\"Hello\"}}");

			assertEquals(2, l.size());
			assertEquals("{\"msg_type\":\"gaze\",\"msg_body\":{\"dir\":\"MidRight\"}}", l.get(0));
			assertEquals("{\"msg_type\":\"speech\",\"msg_body\":{\"text\":\"Hello\"}}", l.get(1));
		}
	}

	@Test
	public void StringContainingTenObjects () {
		String[] thingsBetweenThem = new String[] { "", "  ", "\n", "\r\n" };

		ArrayList<String> messages = new ArrayList<String>();
		for (int i = 0; i < 10; i++)
			messages.add("{\"msg_type\":\"" + UUID.randomUUID().toString() + "\"}");

		String bigMessage = "";
		for (int i = 0; i < 10; i++) {
			bigMessage += thingsBetweenThem[i % thingsBetweenThem.length] + messages.get(i);
		}

		List<String> l = JsonBreakDown.stringsOfIndividualClsses(bigMessage);

		assertEquals(10, l.size());
		for (int i = 0; i < 10; i++) {
			assertEquals(messages.get(i), l.get(i));
		}
	}

}
