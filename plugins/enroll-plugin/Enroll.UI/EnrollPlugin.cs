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
			_remote.RemoveReceiveHandler("enroll.display");
		}

		private readonly Object displayLock = new Object();
		private void display(JObject m)
		{
			lock (displayLock)
			{
				_uiThreadDispatcher.BlockingInvoke(() =>
				{
					string name = "", age = "", gender = "", relationship = "",
						zipcode = "", spouse = "", skype = "", bd = "";
					if (m["name"] != null) name = m["name"].ToString().Trim();
					if (m["age"] != null) if(m["age"].ToString() != "0") 
							age = m["age"].ToString().Trim();
					if (m["gender"] != null) gender = m["gender"].ToString().Trim();
					if (m["relationship"] != null) relationship = m["relationship"].ToString().Trim();
					if (m["zipcode"] != null) if(m["zipcode"].ToString() != "0")
							zipcode = m["zipcode"].ToString().Trim();
					if (m["spouse"] != null) spouse = m["spouse"].ToString().Trim();
					if (m["skypeAccount"] != null) skype = m["skypeAccount"].ToString().Trim();
					if (m["birthday"] != null) bd = m["birthday"].ToString().Trim();
					enrollEntryPanel.label10.Content = name;
					enrollEntryPanel.label11.Content = age;
					enrollEntryPanel.label12.Content = gender;
					enrollEntryPanel.label13.Content = relationship;
					enrollEntryPanel.label15.Content = zipcode;
					enrollEntryPanel.label16.Content = spouse;
					enrollEntryPanel.label18.Content = skype;
					enrollEntryPanel.label14.Content = bd;
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
