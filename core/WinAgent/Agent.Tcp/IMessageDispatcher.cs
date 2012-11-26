using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public interface IMessageDispatcher
	{
		void Send(string messageType, JObject body);
		void RegisterReceiveHandler(string messageType, IMessageHandler handler);

		void RemoveReceiveHandler(string p);
	}
}
