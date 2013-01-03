using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	public interface IRemoteConnection
	{
		event EventHandler<MessageReceivedEventArgs> MessageReceived;
		void Send(string message);
	}
}
