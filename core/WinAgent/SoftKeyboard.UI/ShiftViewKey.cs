using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;

namespace SoftKeyboard.UI
{
	public class ShiftViewKey : CharacterKey
	{
		public ShiftViewKey()
		{
			InitializeComponent();
			Style = (Style)Resources["ShiftViewKeyStyle"];
		}
	}
}
