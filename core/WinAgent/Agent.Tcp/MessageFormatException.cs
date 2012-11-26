using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	[Serializable]
	public class MessageFormatException : Exception
	{
		public MessageFormatException() { }
		public MessageFormatException(string message) : base(message) { }
		public MessageFormatException(string message, Exception inner) : base(message, inner) { }
		protected MessageFormatException(
		  System.Runtime.Serialization.SerializationInfo info,
		  System.Runtime.Serialization.StreamingContext context)
			: base(info, context) { }
	}
}
