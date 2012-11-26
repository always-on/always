using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;

namespace Agent.Tcp
{
	public interface IPlugin : IDisposable
	{
		UIElement GetUIElement();
	}
}
