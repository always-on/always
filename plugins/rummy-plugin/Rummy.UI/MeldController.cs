using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;
using System.Diagnostics;

namespace Rummy.UI
{
    public class MeldController : CardGroupControllerBase
    {
        Player _meldOwner;
        int? _associatedMeldIndex;

        public int? AssociatedMeldIndex
        {
            get { return _associatedMeldIndex; }
            set { _associatedMeldIndex = value; }
        }
        List<Card> _temporaryCardsList = new List<Card>();

        public MeldController(IGameUIServices uiServices, FanCanvas shape, GameState gameState, Player meldOwner)
            : base(uiServices, shape, gameState)
        {
            _meldOwner = meldOwner;
            GameState.BeforeStateChange += BeforeStateChange;
        }

        private IEnumerable<Card> Cards
        {
            get
            {
                if (_associatedMeldIndex == null)
                    return _temporaryCardsList;

                return GameState.GetMelds(_meldOwner)[(int)_associatedMeldIndex];
            }
        }

        public override ReadOnlyCollection<Card> AllCards()
        {
            return new ReadOnlyCollection<Card>(new List<Card>(Cards));
        }

        public bool AssociatedWithGameStateAlready
        {
            get { return _associatedMeldIndex != null; }
        }

        private Meld GetAssociatedMeldFromGameState()
        {
            if (!AssociatedWithGameStateAlready)
                return null;

            Debug.Assert(_associatedMeldIndex != null);

            return GameState.GetMelds(_meldOwner)[(int)_associatedMeldIndex];
        }

        protected override bool DoAcceptDrop(Card card)
        {
            if (Meld.IsValidSubset(Cards.Concat(new[] { card }).ToList()) == false)
                return false;

            Player theOneWhoDraggedTheCard = FindWhosTurnItIs();

            if (GameState.CanMeldLayOff(theOneWhoDraggedTheCard) == false)
                return false;

            if (GameState.GetCards(theOneWhoDraggedTheCard).Contains(card) == false)
                return false;

            if (!AssociatedWithGameStateAlready)
            {
                if (_meldOwner != theOneWhoDraggedTheCard)
                    return false;

                _temporaryCardsList.Add(card);

				if(_temporaryCardsList.Count >= 3)
					SubmitToGameState();

                RenderContents();
                return true;
            }
            else
            {
                var meld = GetAssociatedMeldFromGameState();

                Debug.Assert(meld != null);

                GameState.LayOff(theOneWhoDraggedTheCard, card, meld);

                return true;
            }
        }

        private Player FindWhosTurnItIs()
        {
            Player? player = null;
            foreach (var p in Enum.GetValues(typeof(Player)).Cast<Player>())
            {
                if (GameState.IsPlayersTurn(p))
                {
                    player = p;
                    break;
                }
            }

            if (player == null)
                throw new Exception("Apparantly it is nobody's turn!");

            return (Player)player;
        }

        public void RenderContents()
        {
            SyncShapeChildrenWithModel(Cards, !AssociatedWithGameStateAlready, true);
        }

        public override void DropAcceptedNotification(Card card)
        {
            _temporaryCardsList.Remove(card);
            RenderContents();
        }

        public void AssociateWithMeldInGameState(int index)
        {
            Shape.Dispatcher.Invoke(new Action(() =>
                {
                    _associatedMeldIndex = index;
                    foreach (var c in Shape.Children.OfType<CardShape>())
                    {
                        c.IsEnabled = false;
                    }
                }));
        }

        bool _submittingMeld = false;
        public void BeforeStateChange(State oldState, State newState)
        {
            SubmitToGameState();
        }

        internal void SubmitToGameState()
        {
            if (GameState.CanMeldLayOff(_meldOwner) &&
                    AssociatedWithGameStateAlready == false &&
                    _submittingMeld == false)
            {
                if (_temporaryCardsList.Count >= 3)
                {
                    _submittingMeld = true;
                    GameState.Meld(_meldOwner, _temporaryCardsList);
                    AssociateWithMeldInGameState(GameState.GetMelds(_meldOwner).Count - 1);
                    _submittingMeld = false;
                }
                else
                {
                    var playerCardsShape = UIServices.GetPlayerCardsShape(_meldOwner);
                    foreach (var c in _temporaryCardsList.ToArray())
                    {
                        playerCardsShape.CancelDrag(c);
                        _temporaryCardsList.Remove(c);
                    }

                    RenderContents();
                }
            }
        }
    }
}
