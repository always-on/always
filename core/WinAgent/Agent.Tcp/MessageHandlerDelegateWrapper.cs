using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public class MessageHandlerDelegateWrapper : IMessageHandler
	{
		readonly Action<JObject> _inner;

		public MessageHandlerDelegateWrapper(Action<JObject> inner)
		{
			_inner = inner;
		}

		public void HandleMessage(JObject body)
		{
			_inner(body);
		}
	}
}
