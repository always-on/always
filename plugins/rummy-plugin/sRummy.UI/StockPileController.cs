using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using Rummy;

namespace SRummy.UI
{
    public class StockPileController : CardGroupControllerBase
    {
        public StockPileController(IGameUIServices uiServices, FanCanvas shape, GameState gameState)
            : base(uiServices, shape, gameState)
        {
            RenderContents();

            GameState.Stock.ContentsChanged += new EventHandler(Stock_ContentsChanged);
        }

        void Stock_ContentsChanged(object sender, EventArgs e)
        {
            RenderContents();
        }

        private void RenderContents()
        {
            Shape.Dispatcher.Invoke(new Action(() => {
                Shape.Children.Clear();
                if (GameState.Stock.Count == 0)
                {
                    return;
                }

                foreach (var c in Enumerable.Range(1, GameState.Stock.Count - 1).Take(8).Select(x => new CardShape() { IsEnabled = false, Width = CardWidth, Height = CardHeight, HighlightOnVisibility = false }))
                    Shape.Children.Add(c);

                Shape.Children.Add(new CardShape() { Face = GameState.Stock.Peek().ToString(), HideFace = true, Width = CardWidth, Height = CardHeight, HighlightOnVisibility = false });
            }));
        }

        public override ReadOnlyCollection<Card> AllCards()
        {
            return new ReadOnlyCollection<Card>(new List<Card>() { GameState.Stock.Peek() });
        }
    }
}
