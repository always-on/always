using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Controls;
using System.Windows;

namespace Agent.Tcp
{
	public interface ILayoutWithPluginSupport
	{
        void ShowPlugin(Viewbox pluginContainer);
    }
}