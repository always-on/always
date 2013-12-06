using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Enroll.UI;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;
using System.Windows.Controls;

namespace Enroll.UI
{
	class EnrollPlugin : IPlugin
	{
		EnrollEntryPanel enrollEntryPanel;
		IMessageDispatcher _remote;
		IUIThreadDispatcher _uiThreadDispatcher;
		Viewbox pluginContainer;

		public EnrollPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
		{
			this._remote = remote;
			this._uiThreadDispatcher = uiThreadDispatcher;
			uiThreadDispatcher.BlockingInvoke(() =>
			{
				enrollEntryPanel = new EnrollEntryPanel();
				//Example pluginContainer code
				pluginContainer = new Viewbox();
				pluginContainer.Child = enrollEntryPanel;
				//End of example
			});

			_remote.RegisterReceiveHandler("enroll.display",
				 new MessageHandlerDelegateWrapper(m => display(m)));
		}

		public void Dispose()
		{
			//_remote.RemoveReceiveHandler("");
		}

		private readonly Object displayLock = new Object();
		private void display(JObject m)
		{
			lock (displayLock)
			{
				_uiThreadDispatcher.BlockingInvoke(() =>
				{
					enrollEntryPanel.label10.Content = m["name"];
					enrollEntryPanel.label11.Content = m["age"];
					enrollEntryPanel.label12.Content = m["gender"];
					enrollEntryPanel.label13.Content = m["relationship"];
					enrollEntryPanel.label15.Content = m["zipcode"];
					enrollEntryPanel.label16.Content = m["spouse"];
					enrollEntryPanel.label18.Content = m["skypeAccount"];
					enrollEntryPanel.label14.Content = m["birthday"];
				});
			}
		}

		public System.Windows.UIElement GetUIElement()
		{
			return enrollEntryPanel;
		}

		public Viewbox GetPluginContainer()
		{
			//TODO
			return pluginContainer;
		}
	}
}
