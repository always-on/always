using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Calendar.UI
{
    public class Day
	{
		private long id = 0;
		private int row = 0;
		private int col = 0;
		private bool hasEvents = false;
		private bool isThisMonth = false;
		private bool isToday = false;
		private string label = "";
		public long Id
		{
			get { return id; }
			set { id = value; }
		}
		public int Row
		{
			get { return row; }
			set { row = value; }
		}
		public int Col
		{
			get { return col; }
			set { col = value; }
		}
		public bool HasEvents
		{
			get { return hasEvents; }
			set { hasEvents = value; }
		}
		public bool IsThisMonth
		{
			get { return isThisMonth; }
			set { isThisMonth = value; }
		}
		public bool IsToday
		{
			get { return isToday; }
			set { isToday = value; }
		}
		public string Label
		{
			get { return label; }
			set { label = value; }
		}



        public Day()
            : this(0, 0, 0, false, false, false, "")
        {

        }

        public Day(long id, int row, int col, bool hasEvents, bool isThisMonth, bool isToday, string label)
        {
			this.id = id;
			this.row = row;
			this.col = col;
			this.hasEvents = hasEvents;
			this.isThisMonth = isThisMonth;
			this.isToday = isToday;
			this.label = label;
        }


        public override string ToString()
        {
			return row + " x " + col + " - " + label + " :" + hasEvents+", "+isThisMonth+", "+isToday+" ("+id+")";
        }
    }
}
