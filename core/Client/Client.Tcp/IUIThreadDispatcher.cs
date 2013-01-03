using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	public interface IUIThreadDispatcher
	{
		void BlockingInvoke(Action action);
		void NonBlockingInvoke(Action action);
	}
}
