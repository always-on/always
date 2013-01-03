using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Windows;

namespace Rummy.Startup
{
    // for testing Rummy by itself

    public partial class App : Application
    {
        public App()
        {
           AgentApp.MainWindow.RegisterPlugin("rummy", "AgentApp.RummyPluginCreator,Rummy.UI");
        }
    }
}
