using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace Calendar.UI
{
	public class CalendarPluginCreator : IPluginCreator
	{
		IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
        public CalendarPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
		{
			_uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
		}

		public IPlugin Create(JObject parameters)
		{
			return new CalendarPlugin(_remoteDispatcher, _uiThreadDispatcher);
		}
	}
}
