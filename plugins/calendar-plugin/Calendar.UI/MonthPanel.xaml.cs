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
using System.Collections.Specialized;
using System.Collections.ObjectModel;

namespace Calendar.UI
{
    /// <summary>
    /// Interaction logic for MonthPanel.xaml
    /// </summary>
    public partial class MonthPanel : UserControl
	{
		public class DayCollection : ObservableCollection<Day> { }
		public static readonly DependencyProperty DaysProperty = DependencyProperty.Register("Days", typeof(DayCollection), typeof(MonthPanel));

		public class DayLabelCollection : ObservableCollection<String> { }
		public static readonly DependencyProperty DayLabelsProperty = DependencyProperty.Register("DayLabels", typeof(DayLabelCollection), typeof(MonthPanel));

		public event EventHandler<DaySelectedEventArgs> DaySelected = delegate { };

		private Label[] dayLabels;
        public MonthPanel()
        {
			InitializeComponent();
			dayLabels = new Label[] { day1, day2, day3, day4, day5, day6, day7 };
			SetValue(DaysProperty, new DayCollection());
			SetValue(DayLabelsProperty, new DayLabelCollection());
			((DayCollection)GetValue(DaysProperty)).CollectionChanged += new NotifyCollectionChangedEventHandler(updateHandler);

		}

		private int numRows = 0;
		public int NumRows
		{
			get { return numRows; }
			set
			{
				numRows = value;
				updateHandler(null, null);
			}
		}
		public DayCollection Days
		{
			get { return (DayCollection)GetValue(DaysProperty); }
		}
		public DayLabelCollection DayLabels
		{
			get { return (DayLabelCollection)GetValue(DayLabelsProperty); }
		}

		public string MonthLabel
		{
			get { return (string)monthLabel.Content; }
			set { monthLabel.Content = value; }
		}

		public void setDays(params Day[] events)
		{
			Days.Clear();
			foreach (Day e in events)
				Days.Add(e);
		}

		public void setDayLabels(params string[] events)
		{
			DayLabels.Clear();
			foreach (string e in events)
			{
				DayLabels.Add(e);
			}
		}


		public void updateHandler(Object sender, NotifyCollectionChangedEventArgs eArgs)
		{
			DayCollection collection = (DayCollection)GetValue(DaysProperty);

			contentGrid.Children.Clear();

			contentGrid.RowDefinitions.Clear();

			RowDefinition row = new RowDefinition();
			row.Height = GridLength.Auto;
			contentGrid.RowDefinitions.Add(row);

			contentGrid.Children.Add(monthLabel);

			row = new RowDefinition();
			row.Height = GridLength.Auto;
			contentGrid.RowDefinitions.Add(row);

			for (int i = 0; i < DayLabels.Count(); ++i)
			{
				dayLabels[i].Content = DayLabels[i];
				contentGrid.Children.Add(dayLabels[i]);
			}

			if (collection.Count == 0)
				return;

			for(int i = 0; i<numRows; ++i){
				row = new RowDefinition();
				row.Height = new GridLength(1, GridUnitType.Star);
				contentGrid.RowDefinitions.Add(row);
			}
			
			
			for (int i = 0; i < collection.Count; ++i)
			{
				Day d = collection.ElementAt(i);

				Button dayLabel = new Button();
				dayLabel.Tag = d.Id;
				dayLabel.Content = d.Label;
				dayLabel.Click += OnEventClick;
				if (d.IsToday)
				{
					dayLabel.BorderThickness = new Thickness(6);
					dayLabel.BorderBrush = new SolidColorBrush(Colors.Red);
				}
				if (!d.IsThisMonth)
					dayLabel.Foreground = new SolidColorBrush(Colors.DarkGray);
				if (d.HasEvents)
					dayLabel.Style = (Style)Resources["HighlightedMonthDayLabel"];
				else
					dayLabel.Style = (Style)Resources["MonthDayLabel"];
				Grid.SetRow(dayLabel, d.Row+2);
				Grid.SetColumn(dayLabel, d.Col);

				contentGrid.Children.Add(dayLabel);
			}

		}

		void OnEventClick(object sender, RoutedEventArgs e)
		{
			Button source = (Button)sender;
			DaySelected.Invoke(sender, new DaySelectedEventArgs((long)source.Tag));
		}
    }
}
