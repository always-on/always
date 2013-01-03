using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Core
{
	public static class LogUtils
	{
		public static void LogWithTime(string message)
		{
			var dt = DateTime.Now;
			Console.WriteLine(message + " @ " + dt.Minute + ":" + dt.Second + "." + dt.Millisecond);
		}
	}
}
