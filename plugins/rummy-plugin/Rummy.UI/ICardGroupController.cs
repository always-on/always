using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;

namespace Rummy.UI
{
    public interface ICardGroupController
    {
        FanCanvas Shape { get; }
        bool AcceptDrop(Card card);
        void DropAcceptedNotification(Card card);
        Card CardFromShape(CardShape shape);
        ReadOnlyCollection<Card> AllCards();
    }
}
