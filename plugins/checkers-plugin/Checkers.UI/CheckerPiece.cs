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
using System.Windows.Media.Animation;

namespace Checkers.UI
{
    public delegate void Captured();

    public class CheckerPiece : UserControl
    {

        public int col { get; set; }
        public int row { get; set; }

		//public event Captured OnCaptured;
		//public CheckerPiece()
        //{
        //    this.OnCaptured += new Captured(CheckerPiece_OnCaptured);
        //}

        void CheckerPiece_OnCaptured()
        {
            throw new NotImplementedException();
        }
    }
}
