using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public enum State
    {
        Player1Draw = 1,
        Player1MeldLayDiscard = 2,
        Player2Draw = 3,
        Player2MeldLayDiscard = 4,
        Player1Won = 5,
        Player2Won = 6
    }
}
