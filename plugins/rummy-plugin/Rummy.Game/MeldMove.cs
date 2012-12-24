using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public class MeldMove : Move
    {
        private readonly Meld _meld;

        public Meld Meld
        {
            get { return _meld; }
        } 


        public MeldMove(Player player, Meld meld)
            : base (player)
        {
            _meld = meld;
        }

        public override bool Equals(object obj)
        {
            if (obj == null || obj.GetType() != typeof(MeldMove))
                return false;

            var theOther = (MeldMove)obj;

            if (theOther.Player != this.Player)
                return false;

            if (!theOther._meld.Equals(this._meld))
                return false;

            return true;
        }

        public override int GetHashCode()
        {
            return (int)Player + _meld.GetHashCode() * 2;
        }

        public override void Realize(GameState gameState)
        {
            gameState.Meld(Player, Meld);
        }

        public override string ToString()
        {
            return string.Format("Player {0} -- Meld", Player);
        }
    }
}
