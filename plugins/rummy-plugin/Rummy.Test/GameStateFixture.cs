using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    class GameStateFixture
    {
        [Test]
        public void InitialGameState()
        {
            GameState gs = new GameState();
            Assert.AreEqual(31, gs.Stock.Count);
            Assert.AreEqual(10, gs.Player1Cards.Count);
            Assert.AreEqual(10, gs.Player2Cards.Count);
            Assert.AreEqual(1, gs.Discard.Count);
            Assert.AreEqual(State.Player1Draw, gs.CurrentState);
        }

        [Test]
        public void DrawCardNotPlayersTurn()
        {
            GameState gs = new GameState();
            Assert.That(() => gs.DrawCard(Player.Two, PileName.Stock), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerTurn);
        }

        [Test]
        public void DrawCardFromEmptyStock_ThrowsException()
        {
            GameState gs = new GameState();
            gs.Stock.RemoveAll();
            //empty case
            Assert.That(() => gs.DrawCard(Player.One, PileName.Stock), Throws.ArgumentException);
        }

        [Test]
        public void DrawCardFromStock_MovesTheCardsCorrectly()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Stock);

            Assert.AreEqual(30, gs.Stock.Count);

            Assert.AreEqual(1, gs.Discard.Count);
            Assert.AreEqual(11, gs.Player1Cards.Count);
            Assert.AreEqual(10, gs.Player2Cards.Count);
        }

        [Test]
        public void DrawCardFromDiscard_MovesTheCardsCorrectly()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Discard);
            
            Assert.AreEqual(0, gs.Discard.Count);

            Assert.AreEqual(31, gs.Stock.Count);
            Assert.AreEqual(11, gs.Player1Cards.Count);
            Assert.AreEqual(10, gs.Player2Cards.Count);

        }

        [Test]
        public void DrawCardFromDiscardPile_ChangesState()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Discard);
            Assert.AreEqual(State.Player1MeldLayDiscard, gs.CurrentState);
        }

        [Test]
        public void DrawCardFromStockPile_ChangesState()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Stock);
            Assert.AreEqual(State.Player1MeldLayDiscard, gs.CurrentState);
        }

        [Test]
        public void DrawCardFromStockPile_CorrectCard()
        {
            GameState gs = new GameState();
            Card temp = gs.Stock.Peek();

            int playerCardBeforeDrawing = gs.Player1Cards.Count;
            gs.DrawCard(Player.One, PileName.Stock);

            Assert.AreEqual(playerCardBeforeDrawing + 1, gs.Player1Cards.Count);
            CollectionAssert.Contains(gs.Player1Cards, temp);

            Assert.AreEqual(gs.CurrentError, ErrorMessage.NoError);
        }


        [Test]
        public void DiscardCard_NotPlayersTurn()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Discard);
            Assert.That(() => gs.DiscardCard(Player.Two, gs.Player2Cards.First()), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerTurn);
        }

        [Test]
        public void DiscardCard_NotPlayersCard()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Discard);
            Card temp = gs.Stock.Peek();
            Assert.That(() => gs.DiscardCard(Player.One, temp), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerCard);
        }

        [Test]
        public void DiscardCard_ChangesState()
        {
            GameState gs = new GameState();

            gs.DrawCard(Player.One, PileName.Discard);
            Card justSomethingToDiscard = gs.Player1Cards[0];
            gs.DiscardCard(Player.One, justSomethingToDiscard);

            Assert.AreEqual(State.Player2Draw, gs.CurrentState);
        }

        [Test]
        public void DiscardCard_CannotDiscardJustDrawnFromDiscard()
        {
            GameState gs = new GameState();

            Card temp = gs.Discard.Peek();

            gs.DrawCard(Player.One, PileName.Discard);
            Assert.That(() => gs.DiscardCard(Player.One, temp), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.CannotDiscard);
        }

        [Test]
        public void DiscardCard_CanDiscardJustDrawnFromStock()
        {
            GameState gs = new GameState();

            Card temp = gs.Stock.Peek();

            gs.DrawCard(Player.One, PileName.Stock);
            gs.DiscardCard(Player.One, temp);

            Assert.AreEqual(State.Player2Draw, gs.CurrentState);

            Assert.AreEqual(gs.CurrentError, ErrorMessage.NoError);
        }

        [Test]
        public void DiscardCard_DrawDiscardCardNumberCorrect()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Discard);
            Card justSomethingToDiscard = gs.Player1Cards[0];
            gs.DiscardCard(Player.One, justSomethingToDiscard);

            Assert.AreEqual(31, gs.Stock.Count);
            Assert.AreEqual(1, gs.Discard.Count);
            Assert.AreEqual(10, gs.Player1Cards.Count);
            Assert.AreEqual(10, gs.Player2Cards.Count);
        }

        [Test]
        public void DiscardCard_DrawStockCardNumberCorrect()
        {
            GameState gs = new GameState();
            gs.DrawCard(Player.One, PileName.Stock);
            Card justSomethingToDiscard = gs.Player1Cards[0];
            gs.DiscardCard(Player.One, justSomethingToDiscard);

            Assert.AreEqual(30, gs.Stock.Count);
            Assert.AreEqual(2, gs.Discard.Count);
            Assert.AreEqual(10, gs.Player1Cards.Count);
            Assert.AreEqual(10, gs.Player2Cards.Count);
        }

        [Test]
        public void LayOff_NotPlayerTurn()
        {
            GameState gs = new GameState();
            Card justSomethingToLayOff = gs.Player2Cards[0];
            Meld m = new Meld(Suit.Clubs, 3, 3);
            Assert.That(() => gs.LayOff(Player.Two, justSomethingToLayOff, m), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerTurn);
        }

        [Test]
        public void LayOff_StateDoesNotChange()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(4, Suit.Diamonds),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);

            gs.DrawCard(Player.One, PileName.Stock);

            Card c = new Card(4, Suit.Diamonds);
            Meld m = new Meld(Suit.Diamonds, 5, 3);
            gs.LayOff(Player.One, c, m);

            Assert.AreEqual(State.Player1MeldLayDiscard, gs.CurrentState);
        }

        [Test]
        public void LayOff_ValidateCardMovement()
        {
            var cards = new[]
                                {
                                    new Card(6, Suit.Clubs),
                                    new Card(2, Suit.Clubs),
                                    new Card(12, Suit.Spades)
                                };

            Meld m = new Meld(Suit.Clubs, 7, 3);

            GameState gs = CreateGamesStateWithPlayerOneHaving(cards, State.Player1MeldLayDiscard);

            int playerCardBeforeLayOff = gs.Player1Cards.Count;

            gs.LayOff(Player.One, new Card(6, Suit.Clubs), m);
            
            Assert.AreEqual(playerCardBeforeLayOff - 1, gs.Player1Cards.Count);
            //meld contains that card
            CollectionAssert.Contains(m, new Card(6, Suit.Clubs));
            //player no longer has that card
            CollectionAssert.DoesNotContain(gs.Player1Cards, new Card(6, Suit.Clubs));

            Assert.AreEqual(gs.CurrentError, ErrorMessage.NoError);
        }

        [Test]
        public void LayOff_InvalidCombinations_ThrowException()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(4, Suit.Diamonds),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);

            gs.DrawCard(Player.One, PileName.Stock);

            Meld m = new Meld(Suit.Diamonds, 5, 3);

            Assert.That(() => gs.LayOff(Player.One, new Card(1, Suit.Diamonds), m), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.InvalidLayoff);
            Assert.AreNotEqual(gs.CurrentError, ErrorMessage.InvalidMeld);
        }

        [Test]
        public void Meld_NotPlayerTurn()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(9, Suit.Spades),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);
            gs.DrawCard(Player.One, PileName.Stock);

            Meld m = new Meld(new[] {Suit.Clubs, Suit.Diamonds, Suit.Hearts }, 4);

            Assert.That(() => gs.Meld(Player.Two, m), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerTurn);

        }

        [Test]
        public void Meld_PlayerDoesNotHaveAllCards()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(9, Suit.Spades),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);
            gs.DrawCard(Player.One, PileName.Stock);

            var m = new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Hearts }, 9);

            Assert.That(() => gs.Meld(Player.One, m), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.NotPlayerCard);
        }

        [Test]
        public void Meld_StateDoesNotChange()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(9, Suit.Spades),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);

            gs.DrawCard(Player.One, PileName.Stock);
            Meld m = new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 1);
            gs.Meld(Player.One, m);

            Assert.AreEqual(State.Player1MeldLayDiscard, gs.CurrentState);

        }

        [Test]
        public void Meld_InvalidCombinations()
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

            IList<Card> lCard = new List<Card>();
            var c1 = new Card(12, Suit.Spades);
            var c2 = new Card(1, Suit.Diamonds);
            var c3 = new Card(3, Suit.Diamonds);
            lCard.Add(c1);
            lCard.Add(c2);
            lCard.Add(c3);

            Assert.That(() => gs.Meld(Player.One, lCard), Throws.ArgumentException);
            Assert.AreEqual(gs.CurrentError, ErrorMessage.InvalidMeld);
            Assert.AreNotEqual(gs.CurrentError, ErrorMessage.NoError);
        }

        //TODO:  sometimes failed, need to be fixed
        [Test]
        public void Meld_PlayerLoseCard()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(9, Suit.Spades),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);

            gs.DrawCard(Player.One, PileName.Stock);

            int playerCardBeforeLayOff = gs.Player1Cards.Count;
            Meld m = new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 1);
            gs.Meld(Player.One, m);

            Assert.AreEqual(playerCardBeforeLayOff - 3, gs.Player1Cards.Count);

            CollectionAssert.DoesNotContain(gs.Player1Cards, new Card(1, Suit.Clubs));
            CollectionAssert.DoesNotContain(gs.Player1Cards, new Card(1, Suit.Diamonds));
            CollectionAssert.DoesNotContain(gs.Player1Cards, new Card(1, Suit.Spades));

            Assert.AreEqual(gs.CurrentError, ErrorMessage.NoError);
        }

        [Test]
        public void Meld_Player1MeldGetCard()
        {
            var cards = new[]
                            {
                                new Card(1, Suit.Clubs),
                                new Card(1, Suit.Diamonds),
                                new Card(3, Suit.Diamonds),
                                new Card(1, Suit.Spades),
                                new Card(12, Suit.Diamonds),
                                new Card(12, Suit.Hearts),
                                new Card(12, Suit.Clubs),
                                new Card(3, Suit.Spades),
                                new Card(9, Suit.Spades),
                                new Card(8, Suit.Spades),
                            };
            GameState gs =
               CreateGamesStateWithPlayerOneHaving(cards, State.Player1Draw);

            gs.DrawCard(Player.One, PileName.Stock);
            Meld m = new Meld(new[] { Suit.Clubs, Suit.Diamonds, Suit.Spades }, 1);

            Assert.AreEqual(gs.Player1Melds.Count, 0);

            gs.Meld(Player.One, m);

            Assert.AreEqual(gs.Player1Melds.Count, 1);
        }

        private GameState CreateGamesStateWithPlayerOneHaving(Card[] cards, State state)
        {
            return new GameState(cards, state);
        }

        private GameState CreateGamesStateWithPlayerOneHavingCardsAndMelds(Card[] cards, List<Meld> melds, State state)
        {
            return new GameState(cards, melds, state);
        }
    }
}
