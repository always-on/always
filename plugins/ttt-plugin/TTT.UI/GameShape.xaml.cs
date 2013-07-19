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

namespace TTT.UI
{
    /// <summary>
    /// Interaction logic for GameShape.xaml
    /// </summary>
    public partial class GameShape : UserControl
    {
        public GameShape()
        {
            InitializeComponent();
			MakeTheBoardUnplayable();
        }

        public event EventHandler Played = delegate { };

        public void PlayAgentMove(int cellNum)
        {
            this.Dispatcher.Invoke((Action)(() =>
            {
                 string cellName = "cell" + cellNum.ToString();
                 Button button = (Button)tictactoe.FindName(cellName);
                 button.Content = "X";
            }));
        }

        public void Reset()
        {
            for(int i = 1; i < 10; i++)
            {
                ((Button)this.FindName("cell" + i))
                    .Content = null;
            }
        }

		public void MakeTheBoardPlayable() 
		{
			this.Dispatcher.Invoke((Action)(() =>
            {
				board.IsEnabled = true;
			}));
		}

		public void MakeTheBoardUnplayable() 
		{
			this.Dispatcher.Invoke((Action)(() =>
            {
				board.IsEnabled = false;
			}));
		}

        private void Click(object sender, RoutedEventArgs e)
        { 
			Button button = (Button)sender;
            if (button.Content == null)
            {
                button.Content = "O";
                Played(this, new cellEventArg { cellNum = int.Parse((string)button.Tag.ToString()[1].ToString()) });
            }
        
        }
    }

    class cellEventArg : EventArgs
    {
        public int cellNum { get; set; }

    }
}
