using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Rummy.Test
{
    [TestFixture]
    public class DrawMoveFixture
    {
        [Test]
        public void TestEquals()
        {
            var m1 = new DrawMove(Player.One, PileName.Discard);
            var m2 = new DrawMove(Player.Two, PileName.Discard);
            var m3 = new DrawMove(Player.One, PileName.Discard);
            var m4 = new DrawMove(Player.One, PileName.Stock);
            var m5 = new DrawMove(Player.One, PileName.Stock);


            Assert.True(m1.Equals(m3));
            Assert.True(m3.Equals(m1));
            Assert.True(m4.Equals(m5));
            Assert.True(m1.Equals(m1));
            
            Assert.False(m1.Equals(null));

            Assert.False(m1.Equals(m2));
            Assert.False(m2.Equals(m1));
            Assert.False(m1.Equals(m4));
            Assert.False(m5.Equals(m2));
        }
    }
}
