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
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class GameShape : UserControl
    {
        public event EventHandler Played = delegate { };

        public void playAgentMove(int cellNum)
        {
            string cellTag = "cell" + cellNum;
            Button button = (Button)this.FindName(cellTag);
            button.Content = "X";
        }

        public void reset()
        {
            for(int i = 1; i < 10; i++)
            {
                ((Button)this.FindName("cell" + i))
                    .Content = null;
            }
        }

        public GameShape()
        {
            InitializeComponent();
        }

        private void Click(object sender, RoutedEventArgs e)
        { 
        Button button = (Button)sender;
            int count = 0;

            if (button.Content == null)
            {
                button.Content = "O";
                Played(this, new cellEventArg { cellNum = int.Parse((string)button.Tag) });
            }
        
        }
    }

    class cellEventArg : EventArgs
    {
        public int cellNum { get; set; }
    }
}
