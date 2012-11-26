using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Threading;

namespace Agent.Tcp
{
	public class UIThreadDispatcher : IUIThreadDispatcher
	{
		Dispatcher _inner;

		public UIThreadDispatcher(Dispatcher inner)
		{
			this._inner = inner;
		}

		public void NonBlockingInvoke(Action action)
		{
			_inner.BeginInvoke(action);
		}

		public void BlockingInvoke(Action action)
		{
			_inner.Invoke(action);
		}
	}
}
