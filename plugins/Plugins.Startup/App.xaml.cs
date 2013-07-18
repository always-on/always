﻿using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;

namespace Plugins.Startup
{
	// for starting client with all plugins registered here

	public partial class App : Application
	{
		public App()
		{
			AgentApp.MainWindow.RegisterPlugin("keyboard", "SoftKeyboard.UI.SoftKeyboardPluginCreator,SoftKeyboard.UI");
			AgentApp.MainWindow.RegisterPlugin("calendar", "Calendar.UI.CalendarPluginCreator,Calendar.UI");
			AgentApp.MainWindow.RegisterPlugin("enroll", "Enroll.UI.EnrollPluginCreator,Enroll.UI");
			AgentApp.MainWindow.RegisterPlugin("rummy", "AgentApp.RummyPluginCreator,Rummy.UI");
			AgentApp.MainWindow.RegisterPlugin("story", "Story.UI.StoryPluginCreator,Story.UI");
		}
	}
}
