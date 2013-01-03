using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System.Text.RegularExpressions;

namespace Agent.Tcp
{
	public class MessageDispatcherImpl : IMessageDispatcher
	{
		private IRemoteConnection _sender;
		readonly Dictionary<string, IMessageHandler> handlers = new Dictionary<string, IMessageHandler>();
		
		public MessageDispatcherImpl(IRemoteConnection sender)
		{
			this._sender = sender;
			sender.MessageReceived += (o, e) => HandleMessage(e.Message);
		}

		public void HandleMessage(string message)
        {
			foreach (var m in BreakDownToIndividualStrings(message))
            {
				var json = JsonConvert.DeserializeObject<JObject>(m);
				var msgType = json["msg_type"];
				if (msgType != null && msgType is JValue)
				{
					var type = msgType.ToString();
					var body = json["msg_body"] as JObject;

					if (!handlers.ContainsKey(type))
						throw new InvalidMessageTypeException("No handler found for " + type);

					handlers[type].HandleMessage(body);
				}
				else
				{
					throw new MessageFormatException("msg-type missing or not a string");
				}
			}
		}

		public void Send(string messageType, JObject body)
		{
			_sender.Send(CreateJsonMessage(messageType, body));
		}

		private static string CreateJsonMessage(string type, object body)
		{
			dynamic msg = new JObject();
			msg.msg_type = type;
			msg.msg_body = body;

			return JsonConvert.SerializeObject(msg);
		}


		public void RegisterReceiveHandler(string messageType, IMessageHandler handler)
		{
			if (handlers.ContainsKey(messageType))
				throw new InvalidOperationException("A handler for type " + messageType + " already exists");

			handlers.Add(messageType, handler);
		}
		public void RemoveReceiveHandler(string messageType)
		{
			handlers.Remove(messageType);
		}

		public static IEnumerable<string> BreakDownToIndividualStrings(string source)
		{
			bool insideOne = false, foundAtLeastOne = false;
			int notMatched = 0;
			StringBuilder currentOne = null;
			for (int i = 0; i < source.Length; i++)
			{
				if (!insideOne)
				{
					if (source[i] == '{')
					{
						foundAtLeastOne = insideOne = true;
						notMatched = 1;
						currentOne = new StringBuilder();
						currentOne.Append(source[i]);
					}
				}
				else
				{
					if (source[i] == '{')
						notMatched++;

					if (source[i] == '}')
						notMatched--;

					currentOne.Append(source[i]);
					if (notMatched == 0)
					{
						insideOne = false;
						yield return currentOne.ToString();
					}
				}
			}

			if (insideOne)
				yield return currentOne.ToString();

			if (!foundAtLeastOne)
				yield return source;
		}
	}
}
