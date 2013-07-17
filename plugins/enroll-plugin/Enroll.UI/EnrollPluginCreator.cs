using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace Enroll.UI
{
    public class EnrollPluginCreator : IPluginCreator
    {
        IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;

        public EnrollPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
		{
			_uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
		}

		public IPlugin Create(JObject parameters)
		{
			return new EnrollPlugin(_remoteDispatcher, _uiThreadDispatcher);
		}
	}
}

