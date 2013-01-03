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
using System.Windows.Controls.Primitives;

namespace SoftKeyboard.UI
{
	/// <summary>
	/// Interaction logic for RubOutKey.xaml
	/// </summary>
	public partial class RubOutKey
	{
		public static readonly RoutedEvent RubOutEvent;
		static RubOutKey()
		{
			RubOutKey.RubOutEvent = EventManager.RegisterRoutedEvent("RubOut", RoutingStrategy.Bubble, typeof(RoutedEventHandler), typeof(RubOutKey));
		}
		public event RoutedEventHandler RubOut
		{
			add { AddHandler(CharacterKey.KeyTypedEvent, value); }
			remove { RemoveHandler(CharacterKey.KeyTypedEvent, value); }
		}


		public RubOutKey()
		{
			InitializeComponent();
			Style = (Style)Resources["RubOutKeyStyle"];
			Click += new RoutedEventHandler(clickListener);
		}

		private static void clickListener(object sender, RoutedEventArgs e)
		{
			RubOutKey key = (RubOutKey)sender;
			key.RaiseEvent(new RoutedEventArgs(RubOutKey.RubOutEvent, key));
		}

	}
}
