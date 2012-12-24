using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public enum Suit
    {
        Clubs = 1,
        Diamonds = 2,
        Hearts = 3,
        Spades = 4
    }

	public static class SuitExtensions
	{
		public static string ToAbbreviatedString(this Suit suit)
		{
			return suit.ToString()[0].ToString();
		}
	}
}
