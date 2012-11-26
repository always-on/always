using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
	public class Card : IComparable
	{

		protected Suit suit;
		protected Rank rank;

		//public const string ClubsAbbreviation = "C";
		//public const string DiamondsAbbreviation = "D";
		//public const string HeartsAbbreviation = "H";
		//public const string SpadesAbbreviation = "S";

		private bool _faceUp = true;

		public Card(int rank, Suit suit)
			: this((Rank)rank, suit)
		{ }

		public Card(Rank rank, Suit suit)
		{
			if ((rank < Rank.Ace) || (rank > Rank.King)) throw new ArgumentOutOfRangeException("Rank \"" + rank + "\" is an invalid rank for a card.");

			this.suit = suit;
			this.rank = rank;
		}

		/**
		 * Return the rank for this card.
		 * <p>
		 * @return int
		 */
		public Rank getRank()
		{
			return rank;
		}
		/**
		 * Return the suit for this card.
		 * <p>
		 * @return int
		 */
		public Suit getSuit()
		{
			return suit;
		}
		/**
		 * Determine whether the card is an ACE.
		 * <p>
		 * @return bool
		 * @since v1.7
		 */
		public bool isAce()
		{
			return (rank == Rank.Ace);
		}
		/**
		 * Determine whether the Card is a Face Card (Jack, Queen, King).
		 * <p>
		 * @return bool
		 * @since V1.7
		 */
		public bool isFaceCard()
		{
			return ((rank == Rank.Jack) || (rank == Rank.Queen) || (rank == Rank.King));
		}

		/**
		 * Determine whether two cards have the same rank.
		 * <p>
		 * If the card passed in is null, then false is returned.
		 * <p>
		 * @return bool
		 * @param c ks.common.model.Card
		 */
		public bool sameRank(Card c)
		{
			if (c == null) return false;

			return (rank == c.getRank());
		}
		/**
		 * Determine whether the two cards have the same suit.
		 * <p>
		 * If the card passed in is null, then false is returned. 
		 * @return bool
		 * @param c ks.common.model.Card
		 */
		public bool sameSuit(Card c)
		{
			if (c == null) return false;

			return (suit == c.getSuit());
		}

		/**
		 * Return a string reflective of this Card.
		 * If the card is faceDown, then the name of card is returned in brackets, i.e., "[10H]".
		 * If the card is selected, then the string is appended with a "*" character.
		 * <p>
		 * Creation date: (10/1/01 8:50:08 PM)
		 * @return java.lang.string
		 * @since V1.7 returns state information also. To return just the name, use getName().
		 */
		public override string ToString()
		{
			var sb = new StringBuilder();

			sb.Append(suit.ToAbbreviatedString());

			sb.Append(rank.ToAbbreviatedString());

			return sb.ToString();
		}

		/**
		 * Static method for converting a specific suit identifier into its string representation.
		 *
		 * @return java.lang.string
		 * @param suit int
		 * @since V2.0
		 */
		public static string getSuitName(Suit suit)
		{
			return suit.ToString();
		}


		private bool _draggable;
		public bool Draggable
		{
			get
			{
				return _draggable;
			}
			set
			{
				if (value != _draggable)
				{
					_draggable = value;
					DraggableChanged(this, EventArgs.Empty);
				}
			}
		}

		public override bool Equals(Object o)
		{
			if (o == null) { return false; }
			//JJ Edit
			if (o is Card)
			{
				Card other = (Card)o;
				return (other.rank == rank) && (other.suit == suit);
			}

			return false; // no good
		}

		/**
		 * Default hashCode method, in case cards are used as key value for hashtable.
		 */
		public override int GetHashCode()
		{
			return 13 * (int)suit + (int)rank;
		}

		public int CompareTo(object obj)
		{
			if (obj == null || obj.GetType() != typeof(Card))
				return 0;

			Card c = (Card)obj;
			int result = ((int)this.getSuit()).CompareTo((int)c.getSuit());
			if (result == 0)
				result = this.getRank().CompareTo(c.getRank());

			return result;
		}

		public bool FaceUp
		{
			get
			{
				return _faceUp;
			}
			set
			{
				if (value != _faceUp)
				{
					_faceUp = value;
					FaceUpChanged(this, EventArgs.Empty);
				}
			}
		}

		public event EventHandler DraggableChanged = delegate { }, FaceUpChanged = delegate { };

		public Card Clone()
		{
			return new Card(rank, suit);
		}
	}



}
