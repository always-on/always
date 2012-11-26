using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Calendar.UI
{
    /// <summary>
    /// Interaction logic for WeekPanel.xaml
    /// </summary>
    public partial class WeekPanel : UserControl
    {
        private WeekDayPanel[] panels;
        public WeekPanel()
        {
            InitializeComponent();
            panels = new WeekDayPanel[]{ day1, day2, day3, day4, day5, day6, day7, nextWeek };

			day1.setBorders(null, null, day2, day5);
			day2.setBorders(day1, null, day3, day6);
			day3.setBorders(day2, null, day4, day7);
			day4.setBorders(day3, null, null, nextWeek);

			day5.setBorders(null, day1, day6, null);
			day6.setBorders(day5, day2, day7, null);
			day7.setBorders(day6, day3, nextWeek, null);
			nextWeek.setBorders(day7, day4, null, null);

			foreach(var p in panels)
			{
				p.EventSelected += entrySelected;
			}
			clearBorders();
        }

		public event EventHandler<EntrySelectedEventArgs> EventSelected = delegate { };
		void entrySelected(object sender, EntrySelectedEventArgs e)
		{
			EventSelected.Invoke(sender, e);
		}

		public void clearBorders()
		{
			foreach (var p in panels)
				p.clearBorder();
		}

        public void setDay(int panelId, string date, bool isToday, bool isTouchable, params Entry[] events)
        {
            WeekDayPanel panel = panels[panelId];
			panel.Date = date;
			panel.IsToday = isToday;
			panel.IsTouchable = isTouchable;
			panel.setEvents(events);
        }

        public string WeekLabel
        {
            get { return (string)weekLabel.Content; }
            set { weekLabel.Content = value; }
        }
    }
}
