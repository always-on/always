using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Windows.Threading;

namespace AgentApp
{
    public class TicTacToePluginCreator : IPluginCreator
    {
        IUIThreadDispatcher _uiThreadDispatcher;
        IMessageDispatcher _remoteDispatcher;
        public TicTacToePluginCreator(IUIThreadDispatcher uiThreadDispatcher, IMessageDispatcher remoteDispatcher)
        {
            _uiThreadDispatcher = uiThreadDispatcher;
            _remoteDispatcher = remoteDispatcher;
        }

        public IPlugin Create(JObject parameters)
        {
            return new TicTacToePlugin(_remoteDispatcher, _uiThreadDispatcher);
        }

    }
}
