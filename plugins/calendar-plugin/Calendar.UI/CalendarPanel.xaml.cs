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
	/// Interaction logic for CalendarPanel.xaml
	/// </summary>
	public partial class CalendarPanel : UserControl
	{
		public CalendarPanel()
		{
			InitializeComponent();
			getDayPanel().EventSelected += entrySelected;
			getWeekPanel().EventSelected += entrySelected;
			getMonthPanel().DaySelected += daySelected;

			getDayPanel().Visibility = System.Windows.Visibility.Collapsed;
			getWeekPanel().Visibility = System.Windows.Visibility.Collapsed;
			getMonthPanel().Visibility = System.Windows.Visibility.Collapsed;
		}
		public event EventHandler<EntrySelectedEventArgs> EventSelected = delegate { };
		void entrySelected(object sender, EntrySelectedEventArgs e)
		{
			EventSelected.Invoke(sender, e);
		}
		public event EventHandler<DaySelectedEventArgs> DaySelected = delegate { };
		void daySelected(object sender, DaySelectedEventArgs e)
		{
			DaySelected.Invoke(sender, e);
		}
		public WeekDayPanel getDayPanel()
		{
			return dayView;
		}

		public WeekPanel getWeekPanel()
		{
			return weekView;
		}

		public MonthPanel getMonthPanel()
		{
			return monthView;
		}
	}
}
