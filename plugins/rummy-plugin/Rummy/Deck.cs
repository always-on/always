/**
 * Models a deck of cards as a <code>Stack</code>.
 * <p>
 * Note: Cards are only removed from the top of the deck, but during the initialization
 * routine, this subclass makes detailed use of the actual structure of cards within a
 * stack.
 * <p>
 * When a Deck is created, it is initially empty. The proper way to create a deck is
 * as follows:<p>
 * <pre>
 * Deck d = new Deck ("personalName");<br>
 * d.create (778726);<br>
 * </pre>
 * The <code>create</code> method will randomly shuffle the deck using a deterministic
 * algorithm using 778726 as the seed for pseudo-random shuffling.
 * <p>
 * Creation date: (9/30/01 10:42:44 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
	public class Deck : Stack
	{
		/** Special order Aces then Deuces, etc.... */
		public const int OrderByRank = -1;

		/** Special order. Clubs, then Diamonds, etc... */
		public const int OrderBySuit = -2;

		/**
		 * Create a deck using the prespecified shuffling algorithm based
		 * upon this seed value.
		 * <p>
		 * This method first removes all cards and then deals with
		 * the initializations.
		 * 
		 * This method invokes the <code>initialize</code> method to properly construct the
		 * full deck before the shuffling algorithm commences. Note that this method makes 
		 * detailed use of the internal structure of a Stack.
		 * <p>
		 * Added "-1" as a special sorted deck by suit. Added "-2" as a special
		 * sorted deck by rank.
		 * 
		 * Creation date: (9/30/01 10:44:19 PM)
		 * @param seed int
		 */
		public void Create(int seed)
		{
			Create(seed, new Card[0]);
		}

		public void Create(int seed, ICollection<Card> except)
		{
			RemoveAll();

			if (seed == OrderByRank)
			{
				InsertFullDeckByRank(except);
				return;
			}

			InsertFullDeckBySuit(except);

			// special seeds 
			if (seed == -1 || seed == -2) return;

			Shuffle(seed);
		}

		public void Shuffle(int? seed)
		{
			Random rnd = seed == null ? new Random() : new Random((int)seed);

			// Generate 2048 random bytes (0..255)
			byte[] b = new byte[2048];
			rnd.NextBytes(b);

			// Treat each pair of values b[i] and b[i+1] as two cards that should be swapped.
			int size = Count;
			for (int i = 0; i < 2048; i = i + 2)
			{
				int idx1 = Math.Abs(b[i] % size);
				int idx2 = Math.Abs(b[i + 1] % size);

				// Swap
				Card t = _cards[idx1];
				_cards[idx1] = _cards[idx2];
				_cards[idx2] = t;
			}
		}

		protected void InsertFullDeckBySuit(ICollection<Card> except)
		{
			foreach (Suit s in Enum.GetValues(typeof(Suit)))
			{
				foreach (Rank r in Enum.GetValues(typeof(Rank)))
				{
					var card = new Card(r, s);
					if (except.Contains(card) == false)
						Add(card);
				}
			}
		}

		protected void InsertFullDeckByRank(ICollection<Card> except)
		{
			foreach (Rank r in Enum.GetValues(typeof(Rank)))
			{
				foreach (Suit s in Enum.GetValues(typeof(Suit)))
				{
					var card = new Card(r, s);

					if (except.Contains(card) == false)
						Add(card);
				}
			}
		}
	}
}