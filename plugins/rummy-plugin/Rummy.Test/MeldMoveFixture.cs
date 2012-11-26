using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class MeldMoveFixture
    {
        [Test]
        public void TestEquals()
        {

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

            var m1 = new MeldMove(Player.One, meld1);
            var m2 = new MeldMove(Player.Two, meld1);
            var m3 = new MeldMove(Player.One, meld2);
            var m4 = new MeldMove(Player.Two, meld3);
            
            Assert.True(m1.Equals(m1));
            Assert.True(m1.Equals(m3));
            Assert.True(m3.Equals(m1));
            
            Assert.False(m1.Equals(null));

            Assert.False(m1.Equals(m2));
            Assert.False(m4.Equals(m3));
            Assert.False(m4.Equals(m3));

        }
    }
}
