using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Moq;
using Agent.Core;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp.Test
{
    [TestFixture]
    public class AgentControlJsonAdapterFixture
    {
        IAgentControl agent;
        AgentControlJsonAdapter adapter;
        FakeDispatcher dispatcher;

        [SetUp]
        public void SetUp()
        {
            agent = Mock.Of<IAgentControl>();
			var dispatcherMock = new Mock<FakeDispatcher>() { CallBase = true };
			dispatcher = dispatcherMock.Object;
            adapter = new AgentControlJsonAdapter(agent, dispatcher);
        }

		public void HandleMessage(string message)
		{
			var json = JsonConvert.DeserializeObject<JObject>(message);
			var msgType = json["msg_type"];
			if (msgType != null && msgType is JValue)
			{
				var body = json["msg_body"] as JObject;
				dispatcher.GetHanlderFor(msgType.ToString()).HandleMessage(body);
			}
		}

		[Test]
        public void TestSpeech()
        {
            HandleMessage(" { \"msg_type\"  :  \"speech\",    \"msg_body\"  : {   \"text\" :  \"Hello world\"      } }");

            Mock.Get(agent).Verify(x => x.Say("Hello world"));
        }

        [Test]
        public void TestSpeechDoneMessage()
        {
            object doneMessage = null;
            Mock.Get(dispatcher)
                .Setup(x => x.Send("done", It.IsAny<JObject>()))
                .Callback<string, object>((s, x) => doneMessage = x);

            Mock.Get(agent)
                .Raise(x => x.ActionDone += null, new ActionDoneEventArgs("speech"));

            AssertJsonEquals("{ \"action\" : \"speech\" }", JsonConvert.SerializeObject(doneMessage));
        }

        private static void AssertJsonEquals(string expected, string actual)
        {
            if (string.IsNullOrEmpty(expected))
            {
                Assert.IsNullOrEmpty(actual);
                return;
            }

            var expectedJson = JsonConvert.DeserializeObject<JObject>(expected);
            var actualJson = JsonConvert.DeserializeObject<JObject>(actual);

            Assert.IsTrue(JToken.DeepEquals(expectedJson, actualJson));
        }

        [Test]
        public void TestShowMenusDone()
        {
            object doneMessage = null;
            Mock.Get(dispatcher)
                .Setup(x => x.Send("done", It.IsAny<JObject>()))
                .Callback<string, object>((s, x) => doneMessage = x);

            Mock.Get(agent)
                .Raise(x => x.ActionDone += null, new ActionDoneEventArgs("show_menu"));

            AssertJsonEquals("{ \"action\" : \"show_menu\" }", JsonConvert.SerializeObject(doneMessage));
        }

        [Test]
        public void TestShowMenu()
        {
            HandleMessage("{ \"msg_type\" : \"show_menu\", \"msg_body\" : { 'menus' : ['Hi', 'Bye', 'Repeat'] } }");

            Mock.Get(agent)
                .Verify(x => x.ShowMenu(It.Is<IList<string>>(l =>
                        l[0] == "Hi" && l[1] == "Bye" && l[2] == "Repeat"
                    ), false));
        }

        [Test]
        public void TestTurn()
        {
            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"dir\" : \"left\" } }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.Left));

            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"dir\" : \"right\" } }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.Right));

            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"dir\" : \"mid\" } }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.Mid));

            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"dir\" : \"midleft\" } }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.MidLeft));

            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : { \"dir\" : \"midright\" } }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.MidRight));
        }

        [Test]
        public void TestSpeech_EmptyBody()
        {
            HandleMessage("{ \"msg_type\" : \"speech\", \"msg_body\" : {} }");
            Mock.Get(agent)
                .Verify(x => x.Say(It.IsAny<string>()), Times.Never());
        }

        [Test]
        public void TestShowMenu_EmptyList()
        {
            HandleMessage("{ \"msg_type\" : \"show_menu\", \"msg_body\" : {} }");
            Mock.Get(agent)
                .Verify(x => x.ShowMenu(It.Is<IList<string>>(l => l.Count == 0), false));
        }

        [Test]
        public void TestTurn_NoParam_ShouldAssumeMid()
        {
            HandleMessage("{ \"msg_type\" : \"gaze\", \"msg_body\" : {} }");
            Mock.Get(agent)
                .Verify(x => x.Turn(AgentGaze.Mid));
        }

        [Test]
        public void TestMenuSelected()
        {
            object sentMessage = null;

            Mock.Get(dispatcher)
                .Setup(x => x.Send("menu_selected", It.IsAny<JObject>()))
                .Callback<string, object>((s, x) => sentMessage = x);

            Mock.Get(agent)
                .Raise(x => x.UserSelectedButton += null, new UserSelectedButtonEventArgs("Hello, good to see you"));

            AssertJsonEquals("{ \"text\" : \"Hello, good to see you\" }", JsonConvert.SerializeObject(sentMessage));
        }

		public class FakeDispatcher : IMessageDispatcher
		{
			readonly Dictionary<string, IMessageHandler> handlers = new Dictionary<string, IMessageHandler>();

			public virtual void Send(string messageType, JObject body)
			{
			}

			public virtual void RegisterReceiveHandler(string messageType, IMessageHandler handler)
			{
				handlers.Add(messageType, handler);
			}
			public virtual void RemoveReceiveHandler(string messageType)
			{
				handlers.Remove(messageType);
			}


			public IMessageHandler GetHanlderFor(string messageType)
			{
				return handlers[messageType];
			}
		}
    }
}
