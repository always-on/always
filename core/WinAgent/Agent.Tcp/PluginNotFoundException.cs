using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	[Serializable]
	public class PluginNotFoundException : Exception
	{
		public PluginNotFoundException() { }
		public PluginNotFoundException(string message) : base(message) { }
		public PluginNotFoundException(string message, Exception inner) : base(message, inner) { }
		protected PluginNotFoundException(
		  System.Runtime.Serialization.SerializationInfo info,
		  System.Runtime.Serialization.StreamingContext context)
			: base(info, context) { }
	}
}
