using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;

namespace SoftKeyboard.UI
{
    class SoftKeyboardPlugin : IPlugin
    {
		KeyboardPanel keyboard;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;

		public SoftKeyboardPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher, string contextString)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
				keyboard = new KeyboardPanel(contextString, remote);
            });

			//_remote.RegisterReceiveHandler("calendar.display", new MessageHandlerDelegateWrapper(m => display(m)));
        }
		public void Dispose()
		{
			//_remote.RemoveReceiveHandler("");
		}


        public System.Windows.UIElement GetUIElement()
        {
            return keyboard;
        }
    }
}
