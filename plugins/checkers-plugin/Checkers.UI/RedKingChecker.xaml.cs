using System;
using System.Collections.Generic;
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

namespace Checkers.UI
{
	/// <summary>
	/// Interaction logic for RedKingChecker.xaml
	/// </summary>
    public partial class RedKingChecker : CheckerPiece
	{
		public RedKingChecker()
		{
			this.InitializeComponent();
		}

        private void CheckerPiece_OnCaptured()
        {

        }
	}
}