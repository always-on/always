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
		private IMessageDispatcher remote;
		public KeyboardPanel(string contextString, IMessageDispatcher remote)
		{
			this.remote = remote;
			InitializeComponent();
			contextLabel.Content = contextString;
			AddHandler(CharacterKey.KeyTypedEvent, new RoutedEventHandler(keyTypedHandler));
			AddHandler(RubOutKey.RubOutEvent, new RoutedEventHandler(rubOutHandler));
		}

		private static void keyTypedHandler(object sender, RoutedEventArgs e)
		{
			KeyboardPanel panel = (KeyboardPanel)sender;
			CharacterKey key = (CharacterKey)e.OriginalSource;
			string text = key.getShiftAdjustedValue();
			panel.type(text);
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
			if (textBox.Text.Length < 33)
			{
				textBox.AppendText(text);
				keyboard.Shift = false;
				textBox.Focus();
				textBox.CaretIndex = textBox.Text.Length;
				sendUpdateMessage();
			}
			else
				System.Media.SystemSounds.Beep.Play();
		}
		private void rubOut()
		{
			int newLength = textBox.Text.Length-1;
			if(newLength<0)
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
			remote.Send("keyboard.textUpdate", data);
		}
	}
}
