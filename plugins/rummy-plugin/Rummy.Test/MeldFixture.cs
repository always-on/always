using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class MeldFixture
    {
        [Test]
        public void Create_ConsecutiveCards()
        {
            Meld m = new Meld(Suit.Clubs, 3, 3);

            CollectionAssert.AreEquivalent(new[] { new Card(3, Suit.Clubs), new Card(4, Suit.Clubs), new Card(5, Suit.Clubs) }, m);
        }

        [Test]
        public void CreateWithOnlyOneItem_ConsecutiveCards_ThrowsAnException()
        {
            Assert.That(() => new Meld(Suit.Spades, 5, 1), Throws.ArgumentException);
        }
       
        [Test]
        [ExpectedException(typeof(ArgumentException))]
        public void CreateWithOnlyTwoItems_ThrowsAnException()
        {
            new Meld(Suit.Spades, 5, 2);
        }

        [Test]
        public void Create_SameRank()
        {
            IList<Suit> lSuit= new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);

            Meld m = new Meld(lSuit, 8);
            CollectionAssert.AreEquivalent(new[] { new Card(8, Suit.Clubs), new Card(8, Suit.Diamonds), new Card(8, Suit.Hearts) }, m);
        }

        [Test]
        public void Create_UsingListOfCard()
        {
            IList<Card> lCard = new List<Card>();
            Card c1 = new Card(8, Suit.Clubs);
            Card c2 = new Card(8, Suit.Hearts);
            Card c3 = new Card(8, Suit.Diamonds);
            lCard.Add(c1);
            lCard.Add(c2);
            lCard.Add(c3);

            Meld m = new Meld(lCard);

            CollectionAssert.AreEquivalent(new[] { new Card(8, Suit.Clubs), new Card(8, Suit.Diamonds), new Card(8, Suit.Hearts) }, m);
        }

        [Test]
        public void Create_UsingListOfCard_NotValidMeld_ThrowException()
        {
            IList<Card> lCard = new List<Card>();
            Card c1 = new Card(8, Suit.Clubs);
            Card c2 = new Card(9, Suit.Clubs);
            Card c3 = new Card(10, Suit.Diamonds);
            lCard.Add(c1);
            lCard.Add(c2);
            lCard.Add(c3);

            Assert.That(() => new Meld(lCard), Throws.ArgumentException);
        }

        [Test]
        public void CreateWithOnlyTwoItems_SameRank_ThrowsAnException()
        {
            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            Assert.That(() => new Meld(lSuit, 8), Throws.ArgumentException);
        }

        [Test]
        public void AddCard_SameRank()
        {
            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);
            Meld m = new Meld(lSuit, 8);
            Card c = new Card(8, Suit.Diamonds);
            Assert.IsTrue(m.CanAddACard(c));
        }

        [Test]
        public void AddCard_SameRank_AddDifferentRank_ThrowException()
        {
            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);
            Meld m = new Meld(lSuit, 8);
            Card c = new Card(9, Suit.Diamonds);
            Assert.IsFalse(m.CanAddACard(c));
        }

        [Test]
        public void AddCard_ConsecutiveRank()
        {
            Meld m = new Meld(Suit.Spades, 5, 3);
            Card c = new Card(4, Suit.Spades);
            Assert.IsTrue(m.CanAddACard(c));
        }

        [Test]
        public void AddCard_ConsecutiveRank_DifferentSuit_ThrowException()
        {
            Meld m = new Meld(Suit.Spades, 5, 3);
            Card c = new Card(4, Suit.Clubs);

            Assert.IsFalse(m.CanAddACard(c));
        }

        [Test]
        public void AddCard_ConsecutiveRank_NotConsecutive_ThrowException()
        {
            Meld m = new Meld(Suit.Spades, 5, 3);
            Card c = new Card(2, Suit.Spades);

            Assert.IsFalse(m.CanAddACard(c));
        }
    }
}
