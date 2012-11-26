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

namespace SoftKeyboard.UI
{

	public class SpaceKey : CharacterKey
	{
		public SpaceKey()
		{
			Style = (Style)Resources["SpaceKeyStyle"];
			updateShiftDisplayState();
			ShiftContent = " ";
			NormalContent = " ";
		}
		protected override void updateShiftDisplayState()
		{
			Content = "Space";
		}
	}
}