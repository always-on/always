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
using Agent.Tcp;
using Newtonsoft.Json.Linq;
using System.Media;

namespace SoftKeyboard.UI
{
	/// <summary>
	/// Interaction logic for KeyboardPanel.xaml
	/// </summary>
	public partial class KeyboardPanel : UserControl
	{
		private static bool isOverflow = false;
		private IMessageDispatcher remote;
		private string context;

		public KeyboardPanel(string contextString, IMessageDispatcher remote, bool isNumeric)
		{
			this.remote = remote;
			InitializeComponent();
			contextLabel.Content = contextString;
			context = contextString;
			AddHandler(CharacterKey.KeyTypedEvent, new RoutedEventHandler(keyTypedHandler));
			AddHandler(RubOutKey.RubOutEvent, new RoutedEventHandler(rubOutHandler));
			if (isNumeric)
			{
				hideCharacterKeys();
			}
		}

		private static void keyTypedHandler(object sender, RoutedEventArgs e)
		{
			KeyboardPanel panel = (KeyboardPanel)sender;
			CharacterKey key = (CharacterKey)e.OriginalSource;
			string text = key.getShiftAdjustedValue();
			Typeface typeface = new Typeface(panel.textBox.FontFamily, panel.textBox.FontStyle, panel.textBox.FontWeight, panel.textBox.FontStretch);
			FormattedText ft = new FormattedText(panel.textBox.Text, System.Globalization.CultureInfo.CurrentCulture, System.Windows.FlowDirection.LeftToRight, typeface, panel.textBox.FontSize, Brushes.Black);
			if (ft.Width > panel.textBox.ActualWidth - 5)
			{
				isOverflow = true;
				panel.textBox.Background = new SolidColorBrush(Colors.Red);
				panel.textBox.IsReadOnly = true;
				JObject data = new JObject();
				data.Add("event", "exceed");
				data.Add("prompt", "Enter text: (Use red arrow key to rub out)");
				data.Add("text", panel.textBox.Text);
				panel.remote.Send("keyboard.exceed", data);
				panel.sendUpdateMessage();
				panel.contextLabel.Content = "Too many characters: (Use red arrow key to rub out)";
			}
			else
			{
				panel.type(text);
			}
			e.Handled = true;
		}


		private static void rubOutHandler(object sender, RoutedEventArgs e)
		{
			KeyboardPanel panel = (KeyboardPanel)sender;
			panel.rubOut();
			e.Handled = true;
		}

		private void type(string text)
		{
			textBox.AppendText(text);
			keyboard.Shift = false;
			textBox.Focus();
			textBox.CaretIndex = textBox.Text.Length;
			sendUpdateMessage();
		}
		private void rubOut()
		{
			textBox.Background = new SolidColorBrush(Colors.White);
			isOverflow = false;
			contextLabel.Content = context;
			int newLength = textBox.Text.Length - 1;
			if (newLength < 0)
				newLength = 0;
			textBox.Text = textBox.Text.Substring(0, newLength);
			keyboard.Shift = false;
			textBox.Focus();
			textBox.CaretIndex = textBox.Text.Length;
			sendUpdateMessage();
		}

		private void sendUpdateMessage()
		{
			JObject data = new JObject();
			data["text"] = textBox.Text;
			data["isOverflow"] = isOverflow;
			remote.Send("keyboard.textUpdate", data);
		}

		private void hideCharacterKeys()
		{
			((StackPanel)((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row0")).FindName("NonNumKeyLeft")).Visibility = Visibility.Collapsed;
			((StackPanel)((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row0")).FindName("NonNumKeyRight")).Visibility = Visibility.Collapsed;
			((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row1")).Visibility = Visibility.Collapsed;
			((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row2")).Visibility = Visibility.Collapsed;
			((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row3")).Visibility = Visibility.Collapsed;
			((StackPanel)((StackPanel)keyboard.FindName("verticalPanel")).FindName("row4")).Visibility = Visibility.Collapsed;
		}

	}
}
