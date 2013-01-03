using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;

namespace Story.UI
{
    class StoryPlugin : IPlugin
    {
		//StoryPage story;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;

        public StoryPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
                // story = new StoryPage();
            });
            //
            //_remote.RegisterReceiveHandler("calendar.display",
			//	  new MessageHandlerDelegateWrapper(m => display(m)));
        }

		public void Dispose()
		{
			//_remote.RemoveReceiveHandler("calendar.display");
		}



        public System.Windows.UIElement GetUIElement()
        {
            return null;// story;
        }
    }
}
