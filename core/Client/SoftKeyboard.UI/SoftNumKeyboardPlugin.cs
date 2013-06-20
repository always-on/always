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
    class SoftNumKeyboardPlugin : IPlugin
    {
		NumKeyboardPanel Numkeyboard;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;
        Viewbox pluginContainer;

		public SoftNumKeyboardPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher, string contextString)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
				Numkeyboard = new NumKeyboardPanel(contextString, remote);
                //Example pluginContainer code
                pluginContainer = new Viewbox();
                pluginContainer.Child = Numkeyboard;
                //End of example
            });

			//_remote.RegisterReceiveHandler("calendar.display", new MessageHandlerDelegateWrapper(m => display(m)));
        }
		public void Dispose()
		{   
			//_remote.RemoveReceiveHandler("");
		}

        public Viewbox GetPluginContainer()
        {
            //keyboard
            //TODO
            return pluginContainer;
        }
    }
}
