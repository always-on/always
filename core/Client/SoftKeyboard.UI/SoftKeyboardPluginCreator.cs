using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace SoftKeyboard.UI
{
	public class SoftKeyboardPluginCreator : IPluginCreator
	{
		IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
		public SoftKeyboardPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
		{
			_uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
		}

		public IPlugin Create(JObject parameters)
		{
			return new SoftKeyboardPlugin(_remoteDispatcher, _uiThreadDispatcher, (string)parameters["contextMessage"], (bool)parameters["isNumeric"]);
		}
	}
}
