using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;

namespace Agent.Tcp
{
	public interface IMessageHandler
	{
		void HandleMessage(JObject body);
	}
}
