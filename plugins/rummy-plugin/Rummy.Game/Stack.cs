/**
 * Creation date: (9/30/01 9:45:50 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public class Stack
    {
        public event EventHandler ContentsChanged = delegate { };

        /** initial size of this pile in memory. */
        protected int maxPileSize = 13;

        /** The Cards in the pile. */
        protected Card[] _cards;

        /** The number of actual cards in this pile. */
        protected int numCards = 0;

        /** How many cards in the stack are selected (default to none) */
        protected int numSelectedCards = 0;

        /** If Stack is forced to grow, this is the amount by which it grows. */
        private const int delta = 13;
        /**
         * Construct an empty stack with auto-generated name.
         */
        public Stack()
        {
			// Allocate space for the initial pile
			_cards = new Card[maxPileSize];
		}
        
        public void Add(Card c)
        {
            if (c == null)
                throw new ArgumentNullException("c");

            if (numCards > maxPileSize - 1)
            {
                GrowStack(delta);
            }
            _cards[numCards++] = c;

            FireContentsChanged();
        }

        private void FireContentsChanged()
        {
            ContentsChanged(this, EventArgs.Empty);
        }

        public bool Ascending()
        {
            if (numCards == 0) return true;

            return Ascending(0, numCards);
        }

        /**
         * Determines whether Cards in the Stack are all ascending in rank order.
         * <p>
         * Throws exception if Stack is empty.
         * <p>
         * @return bool
         * @param start the start of the range (zero as first card in Stack)
         * @param end the end of the range (not included in the check, and no greater than Count)
         * @since V2.0  as helper method that can be exposed 
         */
        public bool Ascending(int start, int end)
        {
            if ((start + 1 > end) || (end > numCards) || (end < 1) || (start > numCards - 1) || (start < 0))
                throw new ArgumentException("Invalid arguments [start:" + start + ", end:" + end + ") values.");

            int prevRank = (int)_cards[start].getRank();

            for (int i = start + 1; i < end; i++)
            {
                if ((int)_cards[i].getRank() != ++prevRank)
                {
                    return false;
                }
            }
            return true;
        }

        public int Count
        {
            get { return numCards; }
        }

		public bool Descending()
        {
            if (numCards == 0) return true;

            return Descending(0, numCards);
        }

        /**
         * Determines whether Cards in the Stack are all descending in rank order.
         * <p>
         * Throws Exception if the stack is empty.
         * <p>
         * @return bool
         * @param start the start of the range (zero as first card in Stack)
         * @param end the end of the range (not included in the check, and no greater than Count)
         * @since V2.0  as helper method that can be exposed 
         */
        public bool Descending(int start, int end)
        {
            if ((start + 1 > end) || (end > numCards) || (end < 1) || (start > numCards - 1) || (start < 0))
                throw new ArgumentException("Invalid arguments [start:" + start + ", end:" + end + ") values.");

            int prevRank = (int)_cards[start].getRank();

            for (int i = start + 1; i < end; i++)
            {
                if ((int)_cards[i].getRank() != --prevRank)
                {
                    return false;
                }
            }
            return true;
        }

        public bool Empty
        {
            get { return (numCards == 0); }
        }
        
		/**
         * Remove top card from the pile and return it to the callee (or return null if empty).
         * <p>
         * Generates modelChanged action only if stack is non-empty.
         * <p>
         * @return ks.common.model.Card
         */
        public Card Pop()
        {
            if (numCards == 0) return null;

            numCards--;
            Card c = _cards[numCards];
            _cards[numCards] = null;       // remove reference from array.

            FireContentsChanged();  				  // we have changed state.
            return c;
        }
        public int GetNumSelectedCards()
        {
            return numSelectedCards;
        }

		protected void GrowStack(int delta)
        {
            maxPileSize += delta;

			var oldCards = _cards;
	        _cards = new Card [maxPileSize];

            for (int i = 0; i < maxPileSize - delta; i++)
            {
                _cards[i] = oldCards[i];
            }
        }

		public Card Peek()
        {
            // empty Stack, so return null Card
            if (numCards == 0) return null;

            return _cards[numCards - 1].Clone();
        }

		public Card PeekAt(int idx)
        {
            // empty Stack, so return null Card
            if (numCards == 0) return null;

            if ((idx < 0) || (idx > numCards - 1))
                throw new ArgumentException("Card::peek (int) received illegal argument:" + idx);

            return _cards[idx].Clone();
        }

		public void Push(Stack s)
        {
            if (s == null) return;  // nothing to do.

            int size = s.Count;
            if (size == 0) return;  // nothing to do.

            // must add stack from the bottom up...
            for (int i = 0; i < size; i++)
            {
                Add(s.PeekAt(i));
            }

            FireContentsChanged();  // we have changed state...
        }

		public Rank Rank()
        {
            if (numCards == 0)
            {
                throw new InvalidOperationException("Stack is empty");
            }
            return _cards[numCards - 1].getRank();
        }

		public void RemoveAll()
        {
            if (numCards == 0) return;

            // remove references to cards and reset numCards.
            for (int i = 0; i < numCards; i++)
            {
                _cards[i] = null;
            }

            numCards = 0;
            numSelectedCards = 0;
            FireContentsChanged();
        }

        /**
         * Determines whether Cards in the Stack are all of the same rank. Note that
         * an empty stack returns <code>true</code> as the degenerate case.
         * <p>
         * @return bool
         */
        public bool SameRank()
        {
            if (numCards <= 1) return true;
            return SameRank(0, numCards);
        }

        /**
         * Determines whether Cards in the Stack are all of the same rank. 
         * <p>
         * Throws an exception on an empty stack.
         * <p>
         * @return bool
         * @param start the start of the range (zero as first card in Stack)
         * @param end the end of the range (not included in the check, and no greater than Count)
         * @since V2.0  as helper method that can be exposed 
         */
        public bool SameRank(int start, int end)
        {
            if ((start + 1 > end) || (end > numCards) || (end < 1) || (start > numCards - 1) || (start < 0))
                throw new ArgumentException("Invalid arguments [start:" + start + ", end:" + end + ") values.");

            for (int i = 1; i < numCards; i++)
            {
                if (!_cards[i].sameRank(_cards[i - 1]))
                    return false;
            }

            return true;
        }

        /**
         * Determines whether Cards in the Stack are all of the same suit. Note that
         * an empty stack returns <code>true</code> as the degenerate case.
         * Creation date: (9/30/01 10:16:30 PM)
         * @return bool
         */
        public bool SameSuit()
        {
            if (numCards <= 1) return true;

            return SameSuit(0, numCards);
        }

        /**
         * Determines whether Cards in the Stack are all of the same suit. 
         * <p>
         * Throws an exception on an empty stack.
         * <p>
         * @return bool
         * @param start the start of the range (zero as first card in Stack)
         * @param end the end of the range (not included in the check, and no greater than Count)
         * @since V2.0  as helper method that can be exposed 
         */
        public bool SameSuit(int start, int end)
        {
            if ((start + 1 > end) || (end > numCards) || (end < 1) || (start > numCards - 1) || (start < 0))
                throw new ArgumentException("Stack::sameRank(start,end) received invalid [start:" + start + ", end:" + end + ") values.");

            for (int i = start + 1; i < end; i++)
            {
                // Any suit out of order, return FALSE
                if (!_cards[i].sameSuit(_cards[i - 1]))
                    return false;
            }

            return true;
        }

        /**
         * Return the suit of the topmost card in the Stack. If no card, then returns MAX_VALUE
         * <p>
         * @return int
         * @since V1.5.1 returns MAX_VALUE on error
         * @since V2.0 throws IllegalArgumentException if no cards in stack.
         */
        public Suit Suit()
        {
            if (numCards == 0)
            {
                throw new ArgumentException("Stack::suit() invalid on empty Stack.");
                //return Integer.MAX_VALUE;
            }

            return _cards[numCards - 1].getSuit();
        }

        /**
         * Return string representation of Stack
         * @return string
         * @since V1.5.1 
         */
        public override string ToString()
        {
            if (numCards == 0) return "[Stack:<empty>]";
            StringBuilder sb = new StringBuilder("[Stack:");
            for (int i = 0; i < numCards; i++)
            {
                sb.Append(_cards[i].ToString());
                if (i < numCards - 1) sb.Append(",");
            }
            sb.Append("]");
            return sb.ToString();
        }
    }
}