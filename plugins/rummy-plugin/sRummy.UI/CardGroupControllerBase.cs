using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections.ObjectModel;

namespace SRummy.UI
{
    public abstract class CardGroupControllerBase : ICardGroupController
    {
        public const double CardWidth = 144, CardHeight = 201.6;

        FanCanvas _shape;
        GameState _gameState;
        IGameUIServices _uiServices;

        public IGameUIServices UIServices
        {
            get { return _uiServices; }
        }

        public CardGroupControllerBase(IGameUIServices uiServices, FanCanvas shape, GameState gameState)
        {
            _shape = shape;
            _gameState = gameState;
            _uiServices = uiServices;
        }

        public FanCanvas Shape
        {
            get { return _shape; }
        }

        public GameState GameState
        {
            get { return _gameState; }
        }

        public abstract ReadOnlyCollection<Card> AllCards();

        public bool AcceptDrop(Card card)
        {
            if (MyOwnCardDroppedOnMe(card))
            {
                Shape.CancelDrag(card);
                return true;
            }

            return DoAcceptDrop(card);
        }

        protected virtual bool DoAcceptDrop(Card card)
        {
            return false;
        }

        protected virtual bool MyOwnCardDroppedOnMe(Card card)
        {
            if (AllCards().Any(x => x != null && x.Equals(card)))
                return true;

            return false;
        }

        public virtual Card CardFromShape(CardShape shape)
        {
            var str = shape.GetCardFace();

            foreach (var card in AllCards())
            {
                if (card.ToString() == str)
                    return card;
            }

            return null;
        }

        protected void SyncShapeChildrenWithModel(IEnumerable<Card> cardsInModel)
        {
            SyncShapeChildrenWithModel(cardsInModel, true, true);
        }

        protected void SyncShapeChildrenWithModel(IEnumerable<Card> cardsInModel, bool cardsEnabled, bool showFace)
        {
            SyncShapeChildrenWithModel(cardsInModel, Shape, cardsEnabled, showFace, true);
        }

        protected void SyncShapeChildrenWithModel(IEnumerable<Card> cardsInModel, bool cardsEnabled, bool showFace, bool highlightNewlyCreatedCards)
        {
            SyncShapeChildrenWithModel(cardsInModel, Shape, cardsEnabled, showFace, highlightNewlyCreatedCards);
        }

        protected static void SyncShapeChildrenWithModel(IEnumerable<Card> cardsInModel, FanCanvas fanCanvas, bool cardsEnabled, bool showFace, bool highlightNewlyCreatedCards)
        {
            fanCanvas.Dispatcher.Invoke(new Action(() =>
            {
                foreach (var c in
                        cardsInModel.Where(x => fanCanvas.Children
                            .OfType<CardShape>()
                            .All(y => y.GetCardFace() != x.ToString())))
                {
                    fanCanvas.Children.Add(new CardShape() { Face = c.ToString(), Width = CardWidth, Height = CardHeight, IsEnabled = cardsEnabled, HideFace = !showFace, HighlightOnVisibility = highlightNewlyCreatedCards });
                }

                foreach (var cshape in
                    fanCanvas.Children
                    .OfType<CardShape>()
                    .Where(x => cardsInModel
                        .All(y => y.ToString() != x.GetCardFace()))
                    .ToList())
                {
                    fanCanvas.Children.Remove(cshape);
                }
            }));
        }

        public virtual void DropAcceptedNotification(Card card)
        {
        }
    }
}
