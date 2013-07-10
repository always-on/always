using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace AgentApp
{
    public class TTTPluginCreator : IPluginCreator
    {
        IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
        public TTTPluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
        {
            _uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
        }

        public IPlugin Create(JObject parameters)
        {
            return new TTTPlugin(_remoteDispatcher, _uiThreadDispatcher);
        }

    }
}
