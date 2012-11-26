using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	public class MessageReceivedEventArgs : EventArgs
	{
		string _message;

		public MessageReceivedEventArgs(string message)
		{
			_message = message;
		}

		public string Message { get { return _message; } }
	}
}
