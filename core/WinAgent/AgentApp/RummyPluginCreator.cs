using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace AgentApp
{
	public class RummyPluginCreator : IPluginCreator
	{
		IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
		public RummyPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
		{
			_uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
		}

		public IPlugin Create(JObject parameters)
		{
			bool agentStarts = ShouldAgentStart(parameters);

			return new RummyPlugin(agentStarts, _remoteDispatcher, _uiThreadDispatcher);
		}

		private static bool ShouldAgentStart(JObject parameters)
		{
			bool agentStarts = false;
			if (parameters != null)
			{
				var p = parameters["first_move"];
				if (p is JValue && "agent".Equals(((JValue)p).Value as string, StringComparison.InvariantCultureIgnoreCase))
					agentStarts = true;
			}

			return agentStarts;
		}
	}
}
