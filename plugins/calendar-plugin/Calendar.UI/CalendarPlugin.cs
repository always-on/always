using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Agent.Tcp;
using Calendar.UI;
using System.Windows.Threading;
using Agent.Core;
using Newtonsoft.Json.Linq;

namespace Calendar.UI
{
    class CalendarPlugin : IPlugin
    {
		CalendarPanel calendar;
        IMessageDispatcher _remote;
        IUIThreadDispatcher _uiThreadDispatcher;

        public CalendarPlugin(IMessageDispatcher remote, IUIThreadDispatcher uiThreadDispatcher)
        {
            this._remote = remote;
            this._uiThreadDispatcher = uiThreadDispatcher;
            uiThreadDispatcher.BlockingInvoke(() =>
            {
				calendar = new CalendarPanel();
				calendar.EventSelected += entrySelected;
				calendar.DaySelected += daySelected;

				calendar.getMonthPanel().NumRows = 4;
            });

            _remote.RegisterReceiveHandler("calendar.display",
				  new MessageHandlerDelegateWrapper(m => display(m)));
        }

		void entrySelected(object sender, EntrySelectedEventArgs e)
		{
			JObject data = new JObject();
			data.Add("id", e.EntryId);
			_remote.Send("calendar.entrySelected", data);
		}
		void daySelected(object sender, DaySelectedEventArgs e)
		{
			JObject data = new JObject();
			data.Add("id", e.DayId);
			_remote.Send("calendar.daySelected", data);
		}

		public void Dispose()
		{
			_remote.RemoveReceiveHandler("calendar.display");
		}

		private readonly Object displayLock = new Object();
        private void display(JObject m)
        {
			lock (displayLock)
			{
				_uiThreadDispatcher.BlockingInvoke(() =>
				{
					calendar.getDayPanel().Visibility = System.Windows.Visibility.Collapsed;
					calendar.getWeekPanel().Visibility = System.Windows.Visibility.Collapsed;
					calendar.getMonthPanel().Visibility = System.Windows.Visibility.Collapsed;

					if (m["type"].ToString().Equals("day"))
					{
						calendar.getDayPanel().Date = m["label"].ToString();

						JObject data = (JObject)m["dayData"];
						calendar.getDayPanel().IsToday = (bool)data["isToday"];
						calendar.getDayPanel().IsTouchable = (bool)data["isTouchable"];
						JArray eventData = (JArray)data["entries"];
						Entry[] events = new Entry[eventData.Count];
						for (int j = 0; j < eventData.Count; ++j)
							events[j] = asEvent((JObject)eventData.ElementAt(j));
						calendar.getDayPanel().setEvents(events);
						calendar.getDayPanel().Visibility = System.Windows.Visibility.Visible;
					}
					else if (m["type"].ToString().Equals("week"))
					{
						calendar.getWeekPanel().clearBorders();
						calendar.getWeekPanel().WeekLabel = m["weekLabel"].ToString();
						JArray dayData = (JArray)m["dayData"];
						for (int i = 0; i < dayData.Count; ++i)
						{
							JObject data = (JObject)dayData[i];
							string date = data["date"].ToString();
							JArray eventData = (JArray)data["entries"];
							Entry[] events = new Entry[eventData.Count];
							for (int j = 0; j < eventData.Count; ++j)
								events[j] = asEvent((JObject)eventData.ElementAt(j));
							calendar.getWeekPanel().setDay(i, date, (bool)data["isToday"], (bool)data["isTouchable"], events);
						}
						calendar.getWeekPanel().Visibility = System.Windows.Visibility.Visible;
					}
					else if (m["type"].ToString().Equals("month"))
					{
						calendar.getMonthPanel().MonthLabel = m["monthLabel"].ToString();
						calendar.getMonthPanel().NumRows = (int)m["numRows"];

						JArray dayLabelsData = (JArray)m["dayLabels"];
						string[] dayLabels = new string[dayLabelsData.Count];
						for (int i = 0; i < dayLabels.Count(); ++i)
							dayLabels[i] = (string)dayLabelsData.ElementAt(i);
						calendar.getMonthPanel().setDayLabels(dayLabels);

						JArray dayData = (JArray)m["dayData"];
						Day[] days = new Day[dayData.Count];
						for (int i = 0; i < dayData.Count; ++i)
							days[i] = asDay((JObject)dayData.ElementAt(i));
						calendar.getMonthPanel().setDays(days);
						calendar.getMonthPanel().Visibility = System.Windows.Visibility.Visible;
					}
				});
			}
        }
		private Entry asEvent(JObject m)
		{
			return new Entry(m["id"].ToString(), m["title"].ToString(), m["when"].ToString(), (double)m["start"]);
		}
		private Day asDay(JObject m)
		{
			return new Day((long)m["id"], (int)m["row"], (int)m["col"], (bool)m["hasEvents"], (bool)m["isThisMonth"], (bool)m["isToday"], m["label"].ToString());
		}


        public System.Windows.UIElement GetUIElement()
        {
            return calendar;
        }
    }
}
