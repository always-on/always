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

namespace TicTacToe.UI
{
    /// <summary>
    /// Interaction logic for GameShape.xaml
    /// </summary>
    public partial class GameShape : UserControl
    {
        public GameShape()
        {
            InitializeComponent();
        }

        public event EventHandler Played = delegate { };

        public void playAgentMove(int cellNum)
        {
            this.Dispatcher.Invoke((Action)(() =>
            {
                 string cellName = "cell" + cellNum.ToString();
                 Button button = (Button)tictactoe.FindName(cellName);
                 button.Content = "X";
            }));
        }

        public void reset()
        {
            for(int i = 1; i < 10; i++)
            {
                ((Button)this.FindName("cell" + i))
                    .Content = null;
            }
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
