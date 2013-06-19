using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Diagnostics;
using Rummy;

namespace SRummy.UI
{
    public class DiscardPileController : CardGroupControllerBase
    {
        public DiscardPileController(IGameUIServices uiServices, FanCanvas shape, GameState gameState)
            : base(uiServices, shape, gameState)
        {
            RenderContents();

            gameState.Discard.ContentsChanged += new EventHandler(Discard_ContentsChanged);
        }

        void Discard_ContentsChanged(object sender, EventArgs e)
        {
            RenderContents();
        }

        private void RenderContents()
        {
            Shape.Dispatcher.Invoke(new Action(() =>
            {
                Shape.Children.Clear();
                if (GameState.Discard.Count == 0)
                {
                    return;
                }

                if (GameState.Discard.Count > 2)
                    foreach (var c in Enumerable.Range(1, GameState.Discard.Count - 2).Take(7).Select(x => new CardShape() { IsEnabled = false, Width = CardWidth, Height = CardHeight, HighlightOnVisibility = false }))
                        Shape.Children.Add(c);

                if (GameState.Discard.Count > 1)
                {
                    var secondCard = GameState.Discard.PeekAt(GameState.Discard.Count - 2);
                    Shape.Children.Add(new CardShape() { Face = secondCard.ToString(), Width = CardWidth, Height = CardHeight });
                }

                var firstCard = GameState.Discard.Peek();
                Shape.Children.Add(new CardShape() { Face = firstCard.ToString(), Width = CardWidth, Height = CardHeight });
            }));
        }

        public override ReadOnlyCollection<Card> AllCards()
        {
            return new ReadOnlyCollection<Card>(
                new List<Card>() { GameState.Discard.Peek() }
                );
        }

        protected override bool DoAcceptDrop(Card card)
        {
            var player = FindPlayerOwningTheCard(card);

            if (player == null)
                return false;

            if (GameState.CanDiscard((Player)player) == false)
                return false;

            try
            {
                GameState.DiscardCard((Player)player, card);
            }
            catch (ArgumentException)
            {
                return false;
            }

            return true;
        }

        private Player? FindPlayerOwningTheCard(Card card)
        {
            foreach (var player in Enum.GetValues(typeof(Player)).Cast<Player>())
            {
                if (GameState.GetCards(player).Contains(card))
                    return player;
            }

            return null;
        }
    }
}
