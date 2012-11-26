using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class DiscardMoveFixture
    {
        [Test]
        public void TestEquals()
        {
            var c1 = new Card(5,Suit.Clubs);
            var c2 = new Card(5,Suit.Clubs);
            var c3 = new Card(9,Suit.Clubs);
            var c4 = new Card(5,Suit.Spades);

            var m1 = new DiscardMove(Player.One, c1);
            var m2 = new DiscardMove(Player.Two, c1);
            var m3 = new DiscardMove(Player.Two, c2);
            var m4 = new DiscardMove(Player.Two, c3);
            var m5 = new DiscardMove(Player.One, c4);
            var m6 = new DiscardMove(Player.One, c2);

            Assert.True(m1.Equals(m6));
            Assert.True(m1.Equals(m1));
            Assert.True(m6.Equals(m1));
            Assert.True(m2.Equals(m3));
            
            Assert.False(m1.Equals(null));

            Assert.False(m1.Equals(m2));
            Assert.False(m1.Equals(m5));
            Assert.False(m2.Equals(m4));
            Assert.False(m2.Equals(m1));
        }
    }
}