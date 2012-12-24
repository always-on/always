using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public enum ErrorMessage
    {
        NoError,
        NotPlayerTurn,
        NotPlayerCard,
        CannotDiscard,
        InvalidMeld,
        InvalidLayoff
    }
}
