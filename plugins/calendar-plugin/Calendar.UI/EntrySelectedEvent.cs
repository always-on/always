using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Calendar.UI
{
	public class EntrySelectedEventArgs : EventArgs
	{
		public EntrySelectedEventArgs(string e)
		{
			this.EntryId = e;
		}

		public string EntryId { get; private set; }
	}
}
