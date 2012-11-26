using System;
using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class PossibleMovesFixture
    {
        [Test]
        public void WhenItIsNotMyTurn_GiveMeNothing()
        {
            GameState gs = new GameState();
            Trace.Assert(gs.CurrentState == State.Player1Draw);

            PossibleMoves pa = new PossibleMoves(Player.Two, gs);

            ICollection<Move> moves = pa.Moves();

            Assert.IsNotNull(moves);
            CollectionAssert.IsEmpty(moves);

            gs.DrawCard(Player.One, PileName.Discard);
            Trace.Assert(gs.CurrentState == State.Player1MeldLayDiscard);

            moves = pa.Moves();

            CollectionAssert.IsEmpty(moves);
        }

        [Test]
        public void WhenMyDrawTurn_GiveMeTwoDrawMoves()
        {
            GameState gs = new GameState();

            gs.DrawCard(Player.One, PileName.Discard);
            gs.DiscardCard(Player.One, gs.Player1Cards[0]);

            Trace.Assert(gs.CurrentState == State.Player2Draw);

            var pa = new PossibleMoves(Player.Two, gs);
            var moves = pa.Moves();

            Assert.AreEqual(2, moves.Count);
            CollectionAssert.Contains(moves, new DrawMove(Player.Two, PileName.Discard));
            CollectionAssert.Contains(moves, new DrawMove(Player.Two, PileName.Stock));
        }

        [Test]
        public void WhenMyDiscardTurn_GiveMeDiscardMoves()
        {
            GameState gs = new GameState();

            gs.DrawCard(Player.One, PileName.Discard);

            Trace.Assert(gs.CurrentState == State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);
            var moves = pa.Moves();

            //Assert.AreEqual(11, moves.Count);
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[0]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[1]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[2]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[3]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[4]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[5]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[6]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[7]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[8]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[9]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, gs.Player1Cards[10]));
        }

        [Test]
        public void MyHandHasOnePossibleSameRankMeld_GiveMeThatMeldAndTheDiscardMoves()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs), new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds), new Card(1, Suit.Spades)
                            };
            GameState gs =
                CreateGamesStateWithPlayerOneHaving(cards, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(5, moves.Count);

            CollectionAssert.Contains(moves, new DiscardMove(Player.One, cards[0]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, cards[1]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, cards[2]));
            CollectionAssert.Contains(moves, new DiscardMove(Player.One, cards[3]));

            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 1)));
        }

        [Test]
        public void MyHandHasOnePossibleSameSuitMeld_GiveMeThatMeldAndTheDiscardMoves()
        {
            var cards = new[]
                            {
                                    new Card(1, Suit.Diamonds),
                                    new Card(2, Suit.Diamonds),
                                    new Card(3, Suit.Diamonds),
                                    new Card(1, Suit.Spades)
                            };
            GameState gs =
                CreateGamesStateWithPlayerOneHaving(cards, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(5, moves.Count);

            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One, new Meld(Suit.Diamonds, 1, 3)));
        }


        [Test]
        public void MyHandHasSixPossibleSameRankMeld_GiveMeThatMeldAndTheDiscardMoves()
        {
            var cards = new[]
                            {
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(12, Suit.Spades),
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds)
                            };
            GameState gs =
                CreateGamesStateWithPlayerOneHaving(cards, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(14, moves.Count);

            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 1)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 12)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Hearts }, 12)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Hearts, Suit.Spades }, 12)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Hearts, Suit.Diamonds, Suit.Spades }, 12)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades, Suit.Hearts }, 12)));
        }

        [Test]
        public void MyHandHasThreePossibleSameSuitMeld_GiveMeThatMeldAndTheDiscardMoves()
        {
            var cards = new[]
                            {
                                new Card(5, Suit.Hearts),
                                new Card(10, Suit.Clubs),
                                new Card(12, Suit.Spades),
                                new Card(1, Suit.Diamonds),
                                new Card(2, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(4, Suit.Diamonds),
                                new Card(6, Suit.Diamonds)
                            };
            GameState gs =
                CreateGamesStateWithPlayerOneHaving(cards, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(11, moves.Count);

            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(Suit.Diamonds, 1, 3)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(Suit.Diamonds, 2, 3)));
            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One,
                                                   new Meld(Suit.Diamonds, 1, 4)));
        }

        [Test]
        public void MyHandHasOnePossibleLayOff_GiveMeThatLayOffAndTheDiscardMoves()
        {
            var cards = new[]
                                {
                                    new Card(5, Suit.Hearts),
                                    new Card(10, Suit.Clubs),
                                    new Card(12, Suit.Spades)
                                };

            List<Meld> playerOneMelds = new List<Meld>();
            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);
            Meld m = new Meld(lSuit, 12);
            playerOneMelds.Add(m);

            GameState gs = CreateGamesStateWithPlayerOneHavingCardsAndMelds(cards, playerOneMelds, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(4, moves.Count);

            CollectionAssert.Contains(moves,
                                      new LayOffMove(Player.One, new Card(12, Suit.Spades), m));
        }

        [Test]
        public void MyHandHasTwoPossibleLayOff_GiveMeThatLayOffsAndTheDiscardMoves()
        {
            var cards = new[]
                                {
                                    new Card(6, Suit.Clubs),
                                    new Card(10, Suit.Clubs),
                                    new Card(12, Suit.Spades)
                                };

            List<Meld> playerOneMelds = new List<Meld>();
            Meld m = new Meld(Suit.Clubs, 7, 3);
            playerOneMelds.Add(m);

            GameState gs = CreateGamesStateWithPlayerOneHavingCardsAndMelds(cards, playerOneMelds, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(5, moves.Count);

            CollectionAssert.Contains(moves,
                                      new LayOffMove(Player.One, new Card(10, Suit.Clubs), m));

            CollectionAssert.Contains(moves,
                                        new LayOffMove(Player.One, new Card(6, Suit.Clubs), m));
        }
        //discard, meld, layy off all together
        [Test]
        public void MyHandHasTwoLayOffAndOneMeld_GiveMeThatMeldAndLayOffTheDiscardMoves()
        {
            var cards = new[]
                                {
                                    new Card(6, Suit.Clubs),
                                    new Card(10, Suit.Clubs),
                                    new Card(12, Suit.Spades),
                                    new Card(1, Suit.Diamonds),
                                    new Card(2, Suit.Diamonds),
                                    new Card(3, Suit.Diamonds)
                                };

            List<Meld> playerOneMelds = new List<Meld>();

            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Spades);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);

            Meld m1 = new Meld(lSuit, 3);
            Meld m2 = new Meld(Suit.Clubs, 7, 3);

            playerOneMelds.Add(m1);
            playerOneMelds.Add(m2);

            GameState gs = CreateGamesStateWithPlayerOneHavingCardsAndMelds(cards, playerOneMelds, State.Player1MeldLayDiscard);

            var pa = new PossibleMoves(Player.One, gs);

            var moves = pa.Moves();

            Assert.AreEqual(10, moves.Count);

            CollectionAssert.Contains(moves,
                                      new LayOffMove(Player.One, new Card(10, Suit.Clubs), m2));

            CollectionAssert.Contains(moves,
                                      new LayOffMove(Player.One, new Card(6, Suit.Clubs), m2));

            CollectionAssert.Contains(moves,
                                      new LayOffMove(Player.One, new Card(3, Suit.Diamonds), m1));

            CollectionAssert.Contains(moves,
                                      new MeldMove(Player.One, new Meld(Suit.Diamonds, 1, 3)));
        }

        private GameState CreateGamesStateWithPlayerOneHaving(Card[] cards, State state)
        {
            return new GameState(cards, state);
        }

        private GameState CreateGamesStateWithPlayerOneHavingCardsAndMelds(Card[] cards, List<Meld> melds, State state)
        {
            return new GameState(cards, melds, state);
        }
        //}
    }
}