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
using Rummy;
using Rummy.UI;

namespace TestApp
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class CardShapeTest : Window
    {
        private Card _card = new Card(10, Suit.Hearts);
        public CardShapeTest()
        {
            InitializeComponent();
        }

        private void btnToggleHideFace_Click(object sender, RoutedEventArgs e)
        {
            var s = _card.ToString();
            card.HideFace = !card.HideFace;
        }
    }
}
