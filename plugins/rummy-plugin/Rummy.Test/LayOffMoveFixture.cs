using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class LayOffMoveFixture
    {
        [Test]
        public void TestEquals()
        {
            var c1 = new Card(5, Suit.Clubs);
            var c2 = new Card(5, Suit.Clubs);
            var c3 = new Card(9, Suit.Clubs);
            var c4 = new Card(5, Suit.Spades);

            IList<Suit> lSuit = new List<Suit>();
            lSuit.Add(Suit.Diamonds);
            lSuit.Add(Suit.Clubs);
            lSuit.Add(Suit.Hearts);
            Meld meld1 = new Meld(lSuit, 8);

            IList<Suit> lSuit2 = new List<Suit>();
            lSuit2.Add(Suit.Diamonds);
            lSuit2.Add(Suit.Hearts);
            lSuit2.Add(Suit.Clubs);
            Meld meld2 = new Meld(lSuit2, 8);

            Meld meld3 = new Meld(lSuit2, 2);

            var m1 = new LayOffMove(Player.One, c1, meld1);
            var m2 = new LayOffMove(Player.One, c2, meld1);
            var m3 = new LayOffMove(Player.One, c3, meld1);
            var m4 = new LayOffMove(Player.Two, c1, meld1);
            var m5 = new LayOffMove(Player.Two, c1, meld3);

            Assert.True(m1.Equals(m1));
            Assert.True(m1.Equals(m2));
            Assert.True(m2.Equals(m1));

            Assert.False(m1.Equals(null));

            Assert.False(m1.Equals(m3));
            Assert.False(m3.Equals(m1));
            Assert.False(m4.Equals(m1));
            Assert.False(m4.Equals(m5));

        }
    }
}
