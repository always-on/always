namespace Rummy
{
	public class DrawMove : Move
	{
		private readonly PileName _pile;

		public DrawMove(Player player, PileName pile)
			: base(player)
		{
			_pile = pile;
		}

		public override bool Equals(object obj)
		{
			if (obj == null || obj.GetType() != typeof(DrawMove))
				return false;

			var theOther = (DrawMove)obj;

			if (theOther.Player != this.Player)
				return false;

			if (theOther._pile != this._pile)
				return false;

			return true;
		}

		public override int GetHashCode()
		{
			return (int)Player + (int)_pile * 2;
		}

		public override void Realize(GameState gameState)
		{
			gameState.DrawCard(Player, _pile);
		}

		public override string ToString()
		{
			return string.Format("Player {0} -- Draw from {1} pile", Player, _pile);
		}
	}
}