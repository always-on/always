using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows;

namespace Rummy.UI
{
    public class CardPlaceHolder : ContentControl
    {
        static CardPlaceHolder()
        {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(CardPlaceHolder),
                new FrameworkPropertyMetadata(typeof(CardPlaceHolder)));

        }
    }
}
