using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public class LayOffMove : Move
    {
        private readonly Card _card;

        public Card Card
        {
            get { return _card; }
        }

        private readonly Meld _meld;

        public Meld Meld
        {
            get { return _meld; }
        }

        public Card GetCard()
        {
            return _card;
        }

        public LayOffMove(Player player, Card card, Meld meld)
            : base(player)
        {
            _card = card;
            _meld = meld;
        }

        public override bool Equals(Object obj)
        {
            if (obj == null || obj.GetType() != typeof(LayOffMove))
                return false;

            var theOther = (LayOffMove)obj;

            if (theOther.Player != this.Player)
                return false;

            if (!theOther._card.Equals(this._card))
                return false;

            if (!theOther._meld.Equals(this._meld))
                return false;

            return true;
        }

        public override int GetHashCode()
        {
            return (int)Player + _card.GetHashCode() * 2 + _meld.GetHashCode() * 2;
        }

        public override void Realize(GameState gameState)
        {
            gameState.LayOff(Player, _card, _meld);
        }

        public override string ToString()
        {
            return string.Format("Player {0} -- LayOff {1} to meld", Player, _card);
        }
    }
}
