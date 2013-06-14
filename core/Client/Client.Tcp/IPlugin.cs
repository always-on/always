using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;

namespace Agent.Tcp
{
	public interface IPlugin : IDisposable
	{
        Viewbox GetPluginContainer();
	}
}
