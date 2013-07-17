using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;
using System.Windows.Controls;

namespace SoftKeyboard.UI
{
    class SoftKeyboardPlugin : IPlugin
    {
		KeyboardPanel keyboard;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

		public SoftKeyboardPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher, string contextString, bool isNumeric)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
				keyboard = new KeyboardPanel(contextString, remote, isNumeric);
                pluginContainer = new Viewbox();
                pluginContainer.Child = keyboard;
            });
        }
		public void Dispose()
		{
		}

        public Viewbox GetPluginContainer()
        {
            return pluginContainer;   
        }
    }
}
