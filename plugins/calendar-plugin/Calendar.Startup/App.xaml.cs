using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;

namespace Calendar.Startup
{
    // for testing Calendar by itself

    public partial class App : Application
    {
        public App()
        {
            AgentApp.MainWindow.RegisterPlugin("calendar", "Calendar.UI.CalendarPluginCreator,Calendar.UI");
        }
    }
}
