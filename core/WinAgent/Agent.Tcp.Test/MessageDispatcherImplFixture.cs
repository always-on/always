using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Moq;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp.Test
{
	[TestFixture]
	public class MessageDispatcherImplFixture
	{
		IRemoteConnection sender;
		MessageDispatcherImpl dispatcher;

		[SetUp]
		public void SetUp()
		{
			sender = Mock.Of<IRemoteConnection>();
			dispatcher = new MessageDispatcherImpl(sender);
		}

		[Test]
		public void TestDispatch()
		{
			var handler = Mock.Of<IMessageHandler>();
			dispatcher.RegisterReceiveHandler("gaze", handler);
			dispatcher.RegisterReceiveHandler("speech", Mock.Of<IMessageHandler>());

			dispatcher.HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"terrible\" : \"yet awesome\" } }");

			var expected = new JObject();
			expected["terrible"] = "yet awesome";
			Mock.Get(handler)
				.Verify(x => x.HandleMessage(It.Is<JObject>(actual => JToken.DeepEquals(expected, actual))));
		}

		[Test]
		public void WhenMessageDoesNotHaveAType_ShouldThrowException()
		{
			Assert.Throws<MessageFormatException>(() =>
					dispatcher.HandleMessage("{ \"msg_body\" : { \"situation\" : \"red\" } }"));
		}

		[Test]
		public void WhenMessageTypeIsNotAString_ShouldThrowException()
		{
			Assert.Throws<MessageFormatException>(() =>
				dispatcher.HandleMessage("{ \"msg_type\" : { \"an object\" : \"instead of a string\" } }"));

			Assert.Throws<MessageFormatException>(() =>
				dispatcher.HandleMessage("{ \"msg_type\" : [ \"an array\", \"instead of a string\" ] }"));
		}

		[Test]
		public void WhenThereIsNoHandlerForTheType_ShouldThrowException()
		{
			var m = "{\"msg_type\" : \"hand_wave\", \"msg_body\" : { } }";

			dispatcher.RegisterReceiveHandler("gaze", Mock.Of<IMessageHandler>());

			Assert.Throws<InvalidMessageTypeException>(() => dispatcher.HandleMessage(m));
		}

		[Test]
		public void WhenThereIsNoBodyInTheMessage_ShouldPassOnNullToHandler()
		{
			var handler = Mock.Of<IMessageHandler>();
			dispatcher.RegisterReceiveHandler("speech", handler);

			var m = "{ \"msg_type\" : \"speech\" }";
			dispatcher.HandleMessage(m);

			Mock.Get(handler)
				.Verify(x => x.HandleMessage(null));
		}

		[Test]
		public void IfHandlerForMessageTypeAlreadyExists_ShouldThrowException()
		{
			dispatcher.RegisterReceiveHandler("speech", Mock.Of<IMessageHandler>());

			Assert.Throws<InvalidOperationException>(() => dispatcher.RegisterReceiveHandler("speech", Mock.Of<IMessageHandler>()));
		}
	}
}
