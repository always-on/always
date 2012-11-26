using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;


namespace Rummy.Test
{
    [TestFixture]
    public class CardTest
    {
        [Test]
        public void TestCardCompareDifferentSuit()
        {
            Card c1 = new Card(3, Suit.Clubs);
            Card c2 = new Card(3, Suit.Diamonds);

            Assert.IsTrue(c1.CompareTo(c2) < 0);
            Assert.IsTrue(c2.CompareTo(c1) > 0);
            Assert.IsTrue(c2.CompareTo(c2) == 0);

            Card c3 = new Card(3, Suit.Hearts);
            Card c4 = new Card(3, Suit.Spades);
            Assert.IsTrue(c3.CompareTo(c4) < 0);
            Assert.IsTrue(c2.CompareTo(c4) < 0);
            Assert.IsTrue(c1.CompareTo(c4) < 0);
            Assert.IsTrue(c2.CompareTo(c3) < 0);
            Assert.IsTrue(c1.CompareTo(c3) < 0);
        }

        [Test]
        public void testCardCompareSameSuit()
        {
            Card c1 = new Card(3, Suit.Clubs);
            Card c2 = new Card(4, Suit.Clubs);

            Assert.IsTrue(c1.CompareTo(c2) < 0);
            Assert.IsTrue(c2.CompareTo(c1) > 0);
            Assert.IsTrue(c2.CompareTo(c2) == 0);
        }

		[Test]
		public void ConstructorValidatesTheRank()
		{
			new Card(Rank.Ace, Suit.Clubs);

			new Card(Rank.Eight, Suit.Clubs);

			new Card(Rank.King, Suit.Clubs);

			Assert.Throws<ArgumentOutOfRangeException>(() =>
					new Card((Rank)0, Suit.Clubs));
		}
    }
}
