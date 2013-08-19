namespace Rummy
{
    public class DiscardMove : Move
    {
        private readonly Card _card;

        public DiscardMove(Player player, Card card)
            : base(player)
        {
            _card = card;
        }

        public override bool Equals(object obj)
        {
            if (obj == null || obj.GetType() != typeof(DiscardMove))
                return false;

            var theOther = (DiscardMove)obj;

            if (theOther.Player != this.Player)
                return false;

            if (!theOther._card.Equals(this._card))
                return false;

            return true;
        }

        public Card GetCard(){
            return _card;
        }

        public override int GetHashCode()
        {
            return (int)Player + (int)_card.GetHashCode() * 2;
        }

        public override void Realize(GameState gameState)
        {
            gameState.DiscardCard(Player, _card);
        }

        public override string ToString()
        {
            return string.Format("Player {0} -- Discard {1}", Player, _card);
        }
    }
}