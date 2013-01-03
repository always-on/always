using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Agent.Tcp
{
	[Serializable]
	public class InvalidMessageTypeException : Exception
	{
		public InvalidMessageTypeException() { }
		public InvalidMessageTypeException(string message) : base(message) { }
		public InvalidMessageTypeException(string message, Exception inner) : base(message, inner) { }
		protected InvalidMessageTypeException(
		  System.Runtime.Serialization.SerializationInfo info,
		  System.Runtime.Serialization.StreamingContext context)
			: base(info, context) { }
	}
}
