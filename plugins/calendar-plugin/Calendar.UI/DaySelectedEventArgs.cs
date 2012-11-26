using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Calendar.UI
{
	public class DaySelectedEventArgs : EventArgs
	{
		public DaySelectedEventArgs(long id)
		{
			this.DayId = id;
		}

		public long DayId { get; private set; }
	}
}
