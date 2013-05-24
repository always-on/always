using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Input;
using System.Windows;

namespace sRummy.UI
{
    public class CardDropEventArgs : RoutedEventArgs
    {
        public CardDropEventArgs(CardShape cardShape, MouseEventArgs mouseEventArgs)
        {
            Init(cardShape, mouseEventArgs);
        }

        private void Init(CardShape cardShape, MouseEventArgs mouseEventArgs)
        {
            this.CardShape = cardShape;
            this.MouseEventArgs = mouseEventArgs;
        }

        public CardDropEventArgs(CardShape cardShape, MouseEventArgs mouseEventArgs, RoutedEvent routedEvent)
            : base(routedEvent)
        {
            Init(cardShape, mouseEventArgs);
        }

        public CardDropEventArgs(CardShape cardShape, MouseEventArgs mouseEventArgs, RoutedEvent routedEvent, object source)
            : base(routedEvent, source)
        {
            Init(cardShape, mouseEventArgs);
        }

        public CardShape CardShape { get; private set; }
        public MouseEventArgs MouseEventArgs { get; private set; }
    }
}
