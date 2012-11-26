using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Calendar.UI
{
	public class DaySelectedEventArgs : EventArgs
	{
		public DaySelectedEventArgs(string e)
		{
			this.EntryId = e;
		}

		public string EntryId { get; private set; }
	}
}
