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
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.Windows.Media.Effects;

namespace Calendar.UI
{
    /// <summary>
    /// Interaction logic for WeekDayPanel.xaml
    /// </summary>
    public partial class WeekDayPanel : UserControl
    {


        public class EventCollection : ObservableCollection<Entry> { }

		private EventCollection events = new EventCollection();


		public event EventHandler<EntrySelectedEventArgs> EventSelected = delegate { };

        public string Date
        {
            get { return (string)dateLabel.Content; }
            set { dateLabel.Content = value; }
		}
		private bool isToday = false;
		public bool IsToday
		{
			get { return isToday; }
			set
			{
				isToday = value;
				if (isToday)
				{
					setBorderToday();
				}
			}
		}
		private bool isTouchable = false;
		public bool IsTouchable
		{
			get { return isTouchable; }
			set
			{
				isTouchable = value;
				eventsUpdated();
			}
		}

        private double minHour = 8;
        public double MinHour
        {
            get { return minHour; }
            set { minHour = value; }
        }
        private double maxHour = 20;
        public double MaxHour
        {
            get { return maxHour; }
            set { maxHour = value; }
        }


        public WeekDayPanel()
        {
            InitializeComponent();
			setBorders(null, null, null, null);
        }

		private Border left;
		private Border top;
		private Border right;
		private Border bottom;
		public void setBorders(WeekDayPanel left, WeekDayPanel top, WeekDayPanel right, WeekDayPanel bottom)
		{
			if (left == null)
				leftBorder.BorderThickness = new Thickness(6, 0, 0, 0);
			else
			{
				this.left = left.rightBorder;
				leftBorder.BorderThickness = new Thickness(3, 0, 0, 0);
			}

			if (top == null)
				topBorder.BorderThickness = new Thickness(0, 6, 0, 0);
			else
			{
				this.top = top.bottomBorder;
				topBorder.BorderThickness = new Thickness(0, 3, 0, 0);
			}

			if (right == null)
				rightBorder.BorderThickness = new Thickness(0, 0, 6, 0);
			else
			{
				this.right = right.leftBorder;
				rightBorder.BorderThickness = new Thickness(0, 0, 3, 0);
			}

			if (bottom == null)
				bottomBorder.BorderThickness = new Thickness(0, 0, 0, 6);
			else
			{
				this.bottom = bottom.topBorder;
				bottomBorder.BorderThickness = new Thickness(0, 0, 0, 3);
			}
		}
		public void setBorderToday()
		{
			leftBorder.BorderBrush = new SolidColorBrush(Colors.Red);
			if(left!=null)
				left.BorderBrush = new SolidColorBrush(Colors.Red);

			topBorder.BorderBrush = new SolidColorBrush(Colors.Red);
			if (top != null)
				top.BorderBrush = new SolidColorBrush(Colors.Red);

			rightBorder.BorderBrush = new SolidColorBrush(Colors.Red);
			if (right != null)
				right.BorderBrush = new SolidColorBrush(Colors.Red);

			bottomBorder.BorderBrush = new SolidColorBrush(Colors.Red);
			if (bottom != null)
				bottom.BorderBrush = new SolidColorBrush(Colors.Red);
		}
		public void clearBorder()
		{
			leftBorder.BorderBrush = new SolidColorBrush(Colors.Silver);
			topBorder.BorderBrush = new SolidColorBrush(Colors.Silver);
			rightBorder.BorderBrush = new SolidColorBrush(Colors.Silver);
			bottomBorder.BorderBrush = new SolidColorBrush(Colors.Silver);
		}


        public void eventsUpdated()
		{

            contentGrid.RowDefinitions.Clear();

            contentGrid.Children.Clear();

            if (events.Count == 0)
                return;

            RowDefinition row = new RowDefinition();
            double diff = events.ElementAt(0).RawTime - minHour;
            if (diff < 0)
                diff = 0;
            row.Height = new GridLength(diff, GridUnitType.Star);
			contentGrid.RowDefinitions.Add(row);
			Console.WriteLine("--------");
			Console.WriteLine(diff);

			for (int i = 0; i < events.Count; ++i)
			{
				Entry e = events.ElementAt(i);
				double nextTime = maxHour;
				if (i < events.Count - 1)
				{
					nextTime = events.ElementAt(i + 1).RawTime;
				}
				diff = nextTime - e.RawTime;
				if (diff < 1)
					diff = 0;
				Console.WriteLine(diff);

				row = new RowDefinition();
				row.Height = new GridLength(diff, GridUnitType.Star);
				contentGrid.RowDefinitions.Add(row);

				DockPanel entryPanel = new DockPanel();
				entryPanel.VerticalAlignment = System.Windows.VerticalAlignment.Top;

				Label timeLabel = new Label();
				timeLabel.Content = e.When;
				timeLabel.Style = (Style)Resources["EntryDate"];
				DockPanel.SetDock(timeLabel, Dock.Left);
				entryPanel.Children.Add(timeLabel);

				Button titleLabel = new Button();
				titleLabel.Tag = e.ID;
				titleLabel.Content = e.What;
				titleLabel.Style = (Style)Resources["EntryTitle"];
				titleLabel.Click += OnEventClick;
				entryPanel.Children.Add(titleLabel);

				if (IsTouchable)
				{
					titleLabel.BorderThickness = new Thickness(0, 0, 4, 4);
					titleLabel.BorderBrush = new SolidColorBrush(Colors.DarkGray);
				}

				entryPanel.Measure(new Size(ActualWidth, Double.PositiveInfinity));
				double x = entryPanel.DesiredSize.Height;
				row.MinHeight = x;
				Grid.SetRow(entryPanel, i + 1);


				contentGrid.Children.Add(entryPanel);
			}
        }

		void OnEventClick(object sender, RoutedEventArgs e) {
			Button source = (Button)sender;
			EventSelected.Invoke(sender, new EntrySelectedEventArgs((string)source.Tag));
		}

		public void setEvents(params Entry[] eventsArray)
		{
			events.Clear();
			foreach (Entry e in eventsArray)
				events.Add(e);
			eventsUpdated();
		}
	}
}
