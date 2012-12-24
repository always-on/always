package edu.wpi.always.test.client;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.gson.*;

import edu.wpi.always.client.*;

//ToDo:
// - when text passed to handleMessage() is null
// - when text passed to handleMessage() is not formatted correctly

public class UIMessageDispatcherImplFixture {

	private RemoteConnection conn;
	private UIMessageDispatcherImpl subject;

	@Before
	public void setUp () {
		conn = new DummyConnection();
		subject = new UIMessageDispatcherImpl(conn);
	}

	@Test
	public void testDispatch () {
		MockMessageHandler h1 = new MockMessageHandler();
		subject.registerReceiveHandler("gaze", h1);
		subject.registerReceiveHandler("speech", new MockMessageHandler());

		subject.handleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"terrible\" : \"yet awesome\" } }");

		JsonObject expected = new JsonObject();
		expected.addProperty("terrible", "yet awesome");

		sleepToMakeSureContextSwitchHappens();
		
		assertEquals(expected, h1.lastMessageBody);
	}

	@Test(expected = JsonMessageFormatException.class)
	public void whenMessageDoesNotHaveAType_ShouldThrowException () {
		subject.handleMessage("{ \"msg_body\" : { \"situation\" : \"red\" } }");
	}

	@Test(expected = JsonMessageFormatException.class)
	public void whenMessageTypeIsAnObjectInsteadOfString_ShouldThrowException () {
		subject.handleMessage("{ \"msg_type\" : { \"an object\" : \"instead of a string\" } }");
	}

	@Test(expected = JsonMessageFormatException.class)
	public void whenMessageTypeIsAnArraysInsteadOfString_ShouldThrowException () {
		subject.handleMessage("{ \"msg_type\" : [ \"an array\", \"instead of a string\" ] }");
	}

	@Test
	public void whenMessageTypeIsANumber_ShouldAcceptIt () {
		MockMessageHandler h1 = new MockMessageHandler();
		subject.registerReceiveHandler("10", h1);

		subject.handleMessage("{ \"msg_type\" : 10, \"msg_body\" : { \"situation\" : \"red\" } }");

		sleepToMakeSureContextSwitchHappens();
		
		JsonObject expected = new JsonObject();
		expected.addProperty("situation", "red");
		assertEquals(expected, h1.lastMessageBody);
	}

	@Test(expected=InvalidMessageTypeException.class)
	public void whenThereIsNoHandlerForTheType_ShouldThrowException() {
		subject.registerReceiveHandler("gaze", new MockMessageHandler());
		
		subject.handleMessage("{\"msg_type\" : \"hand_wave\", \"msg_body\" : { } }");
	}
	
	@Test
	public void whenThereIsNoBodyInMessage_ShouldPassNullToHandler() {
		MockMessageHandler handler = new MockMessageHandler();
		subject.registerReceiveHandler("gaze", handler);
		
		//later we rely on .lastMessageBody being set to null,
		//so let's init it to a non-null value
		handler.lastMessageBody = new JsonObject();
		
		subject.handleMessage("{ \"msg_type\" : \"gaze\" }");
		
		sleepToMakeSureContextSwitchHappens();
		
		assertNull(handler.lastMessageBody);
	}

	private void sleepToMakeSureContextSwitchHappens() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void whenRegistering_IfAHanlderForTheTypeAlreadyExists_ShouldThrowException() {
		subject.registerReceiveHandler("gaze", new MockMessageHandler());
		
		try {
			MockMessageHandler anotherHandler = new MockMessageHandler();
			subject.registerReceiveHandler("gaze", anotherHandler);
			fail("expected an IllegalStateException");
		} catch(IllegalStateException ex) {
			
		}
	}
	
	private static class DummyConnection implements RemoteConnection {

		@Override
		public void connect () {
		}

		@Override
		public boolean isConnected () {
			return false;
		}

		@Override
		public void beginSend (String message) {
		}

		@Override
		public void removeObserver (TcpConnectionObserver o) {
		}

		@Override
		public void addObserver (TcpConnectionObserver o) {
		}

	}

	private static class MockMessageHandler implements MessageHandler {

		public JsonObject lastMessageBody;

		@Override
		public void handleMessage (JsonObject body) {
			lastMessageBody = body;
		}

	}

}
