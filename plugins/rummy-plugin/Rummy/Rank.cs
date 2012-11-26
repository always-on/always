using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
	public enum Rank
	{
		Ace = 1,
		Two,
		Three,
		Four,
		Five,
		Six,
		Seven,
		Eight,
		Nine,
		Ten,
		Jack,
		Queen,
		King
	}

	public static class RankExtensions
	{
		public static string ToAbbreviatedString(this Rank rank)
		{
			switch (rank)
			{
				case Rank.Ace:
				case Rank.Jack:
				case Rank.Queen:
				case Rank.King:
					return rank.ToString().Substring(0, 1);
				default:
					return ((int)rank).ToString();
			}

		}
	}
}
