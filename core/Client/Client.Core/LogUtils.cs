using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace Agent.Core
{
	public static class LogUtils
	{
		public static void LogWithTime(string message)
		{
			var dt = DateTime.Now;
			Debug.WriteLine(message + " @ " + dt.Minute + ":" + dt.Second + "." + dt.Millisecond);
		}
	}
}
