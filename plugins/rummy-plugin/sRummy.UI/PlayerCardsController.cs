using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Windows;
using System.Collections.Specialized;

namespace sRummy.UI
{
    public class PlayerCardsController : CardGroupControllerBase
    {
        Player _player;
        bool _renderingForTheFirstTime = true;

        public Player Player
        {
            get { return _player; }
        }
        bool _cardsEnabled = true;

        public PlayerCardsController(IGameUIServices uiServices, FanCanvas shape, GameState gameState, Player player)
            : this(uiServices, shape, gameState, player, true) { }

        public PlayerCardsController(IGameUIServices uiServices, FanCanvas shape, GameState gameState, Player player, bool cardsEnabled)
            : base(uiServices, shape, gameState)
        {
            _player = player;
            _cardsEnabled = cardsEnabled;

            if (cardsEnabled)
                shape.SortCards = true;

            shape.Children.Clear();
            RenderContents();

            GameState.GetCards(player).CollectionChanged += new NotifyCollectionChangedEventHandler(PlayerCardsController_CollectionChanged);
        }

        void PlayerCardsController_CollectionChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            RenderContents();
        }

        private void RenderContents()
        {
            SyncShapeChildrenWithModel(GameState.GetCards(_player), _cardsEnabled, _cardsEnabled, !_renderingForTheFirstTime);
            _renderingForTheFirstTime = false;
        }

        public override ReadOnlyCollection<Card> AllCards()
        {
            return new ReadOnlyCollection<Card>(GameState.GetCards(_player));
        }

        //TODO: support the not-implemented-yet error reporting system instead of the exceptions
        protected override bool DoAcceptDrop(Card card)
        {
            if (GameState.Discard.Count > 0 && GameState.Discard.Peek().Equals(card))
            {
                try
                {
                    GameState.DrawCard(_player, PileName.Discard);
                }
                catch (ArgumentException)
                {
                    return false;
                }

                return true;
            }
            else if (GameState.Stock.Peek().Equals(card))
            {
                try
                {
                    GameState.DrawCard(_player, PileName.Stock);
                }
                catch (ArgumentException)
                {
                    return false;
                }

                return true;
            }

            return false;
        }
    }
}
