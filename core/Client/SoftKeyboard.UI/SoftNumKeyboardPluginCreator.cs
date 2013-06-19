using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace SoftKeyboard.UI
{
	public class SoftNumKeyboardPluginCreator : IPluginCreator
	{
		IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
		public SoftNumKeyboardPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
		{
			_uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
		}

		public IPlugin Create(JObject parameters)
		{
			return new SoftNumKeyboardPlugin(_remoteDispatcher, _uiThreadDispatcher, (string)parameters["contextMessage"]);
		}
	}
}
