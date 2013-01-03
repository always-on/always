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
	/// Interaction logic for CharacterKey.xaml
	/// </summary>
	public partial class CharacterKey
	{
		public static readonly DependencyProperty ShiftProperty = DependencyProperty.Register("Shift", typeof(bool), typeof(CharacterKey), new PropertyMetadata(ShiftChangedCallback));
		public bool Shift
		{
			get { return (bool)this.GetValue(ShiftProperty); }
			set { this.SetValue(ShiftProperty, value);}
		}

		public static readonly DependencyProperty OtherContentProperty = DependencyProperty.Register("OtherContent", typeof(string), typeof(CharacterKey));
		public string OtherContent
		{
			get { return (string)this.GetValue(OtherContentProperty); }
			set { this.SetValue(OtherContentProperty, value); }
		}

		private string normalContent;
		private string shiftContent;
		public string NormalContent { get { return normalContent; } set { normalContent = value; updateShiftDisplayState(); } }
		public string ShiftContent { get { return shiftContent; } set { shiftContent = value; updateShiftDisplayState(); } }

		public static readonly RoutedEvent KeyTypedEvent;
		static CharacterKey()
		{
			CharacterKey.KeyTypedEvent = EventManager.RegisterRoutedEvent("KeyTyped", RoutingStrategy.Bubble, typeof(RoutedEventHandler), typeof(CharacterKey));
		}
		public event RoutedEventHandler KeyTyped
		{
			add { AddHandler(CharacterKey.KeyTypedEvent, value); }
			remove { RemoveHandler(CharacterKey.KeyTypedEvent, value); }
		}

		public CharacterKey()
		{
			InitializeComponent();
			Style = (Style)Resources["KeyStyle"];
			updateShiftDisplayState();
			Click+=new RoutedEventHandler(clickListener);
		}

		private static void ShiftChangedCallback(DependencyObject obj, DependencyPropertyChangedEventArgs e)
		{
			CharacterKey key = (CharacterKey)obj;
			key.updateShiftDisplayState();
		}
		protected virtual void updateShiftDisplayState()
		{
			if (Shift)
			{
				Content = shiftContent;
				OtherContent = normalContent;
			}
			else
			{
				Content = normalContent;
				OtherContent = shiftContent;
			}
		}
		public string getShiftAdjustedValue()
		{
			if (Shift)
			{
				return shiftContent;
			}
			else
			{
				return normalContent;
			}
		}


		private static void clickListener(object sender, RoutedEventArgs e)
		{
			CharacterKey key = (CharacterKey)sender;
			key.RaiseEvent(new RoutedEventArgs(CharacterKey.KeyTypedEvent, key));
		}
	}
}
