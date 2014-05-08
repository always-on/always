using System;
using System.Collections.Generic;
using System.Configuration;
using System.Data;
using System.Linq;
using System.Windows;

namespace Story.Startup
{
    // for testing Story by itself

    public partial class App : Application
    {
        public App()
        {
            AgentApp.MainWindow.RegisterPlugin("story", "Story.UI.StoryPluginCreator,Story.UI");
        }
    }
}
