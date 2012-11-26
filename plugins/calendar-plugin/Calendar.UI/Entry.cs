using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Calendar.UI
{
    public class Entry
    {
        private string id = "";
        private string when = "";
        private string what = "";
        private double rawTime = 0;
        public string When
        {
            get { return when; }
            set { when = value; }
        }
        public string What
        {
            get { return what; }
            set { what = value; }
        }
		public double RawTime
		{
			get { return rawTime; }
			set { rawTime = value; }
		}
		public string ID
		{
			get { return id; }
			set { id = value; }
		}

        public Entry()
            : this("", "What", "When", 0)
        {

        }

        public Entry(string id, string what, string when, double rawTime)
        {
            this.id = id;
            this.what = what;
            this.when = when;
            this.rawTime = rawTime;
        }


        public override string ToString()
        {
            return when + " - " + what + " (" + id + ")";
        }
    }
}
