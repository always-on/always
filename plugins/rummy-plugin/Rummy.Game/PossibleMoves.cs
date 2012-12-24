#region

using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;

#endregion

namespace Rummy
{
    public class PossibleMoves
    {
        private readonly GameState _gameState;
        private readonly Player _player;

        public PossibleMoves(Player player, GameState gameState)
        {
            _player = player;
            _gameState = gameState;
        }

        public IList<Move> Moves()
        {
            if (IsMyTurn() == false)
                return new Move[0];

            if (IsDrawTurn())
            {
                return new Move[]
                           {
                               new DrawMove(_player, PileName.Discard),
                               new DrawMove(_player, PileName.Stock),
                           };
            }
            if (IsMeldLayDiscardTurn())
            {
                IList<Card> playerCards = GetPlayerCards();
                int size = playerCards.Count;
                var moves = new List<Move>();
                //discard
                for (int i = 0; i < size; i++)
                {
                    moves.Add(new DiscardMove(_player, playerCards[i]));
                }

                //meld same rank
                moves.AddRange(PossibleSameRankMeldMoves());
                //meld same suit
                moves.AddRange(PossibleSameSuitMeldMoves());
                //layoff
                moves.AddRange(PossibleLayOffMoves());

                return moves;
            }

            return new Move[0];
        }

        private IEnumerable<LayOffMove> PossibleLayOffMoves()
        {
            IList<Meld> playerOneMelds = new List<Meld>();
            IList<Meld> playerTwoMelds = new List<Meld>();

            playerOneMelds = GetPlayerOneMelds();
            playerTwoMelds = GetPlayerTwoMelds();
            //player one
            foreach (var m in playerOneMelds)
            {
                foreach (var card in GetPlayerCards())
                {
                    if (m.CanAddACard(card))
                        yield return new LayOffMove(_player, card, m);
                }
            }
            //player two
            foreach (var m in playerTwoMelds)
            {
                foreach (var card in GetPlayerCards())
                {
                    if (m.CanAddACard(card))
                        yield return new LayOffMove(_player, card, m);
                }
            }
        }

        private IEnumerable<MeldMove> PossibleSameRankMeldMoves()
        {
            var sameRankCards = new List<Card>[13];
            for (int i = 0; i < 13; i++)
                sameRankCards[i] = new List<Card>();

            foreach (var card in GetPlayerCards())
            {
                sameRankCards[(int)card.getRank() - 1].Add(card);
            }

            foreach (var l in sameRankCards)
            {
                if (l.Count >= 3)
                    yield return new MeldMove(_player, new Meld(l));

                if (l.Count == 4)
                {
                    foreach (var c in l)
                        yield return new MeldMove(_player, new Meld(l.Except(new[] {c}).ToList()));
                }
            }
        }

        private IEnumerable<MeldMove> PossibleSameSuitMeldMoves()
        {
            Dictionary<Suit, List<Card>> suitCards = new Dictionary<Suit, List<Card>>();

            foreach (var suit in Enum.GetValues(typeof (Suit)).Cast<Suit>())
                suitCards[suit] = new List<Card>();

            foreach (var card in GetPlayerCards())
                suitCards[card.getSuit()].Add(card);

            IEnumerable<MeldMove> result = Enumerable.Empty<MeldMove>();
            foreach (var suit in suitCards.Keys)
                result = result.Concat(FindMeldFromArrayList(suitCards[suit]));

            return result;
        }

        private IEnumerable<MeldMove> FindMeldFromArrayList(List<Card> cards)
        {
            cards.Sort();
            for (int length = 3; length <= cards.Count; length++)
            {
                foreach(var i in Enumerable.Range(0, cards.Count - length + 1))
                {
                    if ((cards[i].getRank() + length - 1) == (cards[i + length - 1].getRank()))
                    {
                        var returnList = Enumerable.Range(i, length)
                            .Select(x => cards[x])
                            .ToList();

                        yield return new MeldMove(_player, new Meld(returnList));
                    }
                }
            }
        }

        public static void PrintIndexAndValues(IEnumerable myList)
        {
            int i = 0;
            foreach (Object obj in myList)
                Console.WriteLine("\t[{0}]:\t{1}", i++, obj);
            Console.WriteLine();
        }

        private IList<Card> GetPlayerCards()
        {
            if (_player == Player.One)
            {
                return _gameState.Player1Cards;
            }

            return _gameState.Player2Cards;
        }

        private IList<Meld> GetPlayerOneMelds()
        {
            return _gameState.Player1Melds;
        }

        private IList<Meld> GetPlayerTwoMelds()
        {
            return _gameState.Player2Melds;
        }

        private bool IsDrawTurn()
        {
            return _gameState.CurrentState == State.Player1Draw || _gameState.CurrentState == State.Player2Draw;
        }

        private bool IsMeldLayDiscardTurn()
        {
            return _gameState.CurrentState == State.Player1MeldLayDiscard ||
                   _gameState.CurrentState == State.Player2MeldLayDiscard;
        }

        private bool IsMyTurn()
        {
            if (_player == Player.One)
            {
                return _gameState.CurrentState == State.Player1Draw ||
                       _gameState.CurrentState == State.Player1MeldLayDiscard;
            }

            return _gameState.CurrentState == State.Player2Draw ||
                   _gameState.CurrentState == State.Player2MeldLayDiscard;
        }
    }
}