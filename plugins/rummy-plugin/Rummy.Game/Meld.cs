using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Rummy
{
    public class Meld :  IEnumerable<Card>
    {
        private readonly List<Card> _cards = new List<Card>();

        public Meld(Suit suit, int startRank, int count)
        {
            if (count <= 2)
                throw new ArgumentException("Count should be more than 2", "count");

            for(int i = startRank; i < startRank + count; i++)
            {
                Cards.Add(new Card(i, suit));
            }
        }

        private IList<Card> Cards
        {
            get { return _cards; }
        }

        public Meld(IList<Suit> lSuit, int rank)
        {
            if (lSuit.Count <= 2)
                throw new ArgumentException("Count should be more than 2", "count");

            foreach (Suit s in lSuit)
            {
                Cards.Add(new Card(rank, s));
            }
        }

        public Meld(IList<Card> lCard)
        {
            if(IsValid(lCard))
            {
                foreach (Card c in lCard)
                {
                    Cards.Add(c);
                }
            }
            else
                throw new ArgumentException("Cannot form a meld from a list of cards", "lCard");
              
        }

        public Meld()
        {
        }

        /**
         * Default equals method
         */
        public override bool Equals(Object o)
        {
            if (o == null) { return false; }
    
            if (o is Meld)
            {
                Meld other = (Meld)o;

                if (_cards.Count != other._cards.Count)
                    return false;

                foreach (Card c in this._cards)
                {
                    if (!other._cards.Contains(c))
                        return false;
                }
                
            }

            return true;
        }

        /**
         * Default hashCode method
         */
        public override int GetHashCode()
        {
            return Cards[0].GetHashCode() + Cards[1].GetHashCode() * 100 + Cards[2].GetHashCode() * 10000;
        }

        public bool IsValid(IList<Card> cards)
        {
            //less than 3
            if (cards.Count <= 2)
                return false;

            return IsValidSubset(cards);
        }

        public static bool IsValidSubset(IList<Card> cards)
        {
            //do they have same rank
            if (SameRank(cards))
                return true;

            if (SameSuit(cards) && ConsecutiveRank(cards))
                return true;

            return false;
        }

        //do the list of cards have same rank
        public static bool SameRank(IList<Card> cards)
        {
            Rank firstRank = cards[0].getRank();
            foreach (Card c in cards)
            {
                if(c.getRank() != firstRank)
                    return false;
            }
            return true;
        }

        //do the list of cards have same suit
        public static bool SameSuit(IList<Card> lCard)
        {
            Card[] cArray = lCard.ToArray();
            Suit firstSuit = cArray[0].getSuit();
            foreach (Card c in cArray)
            {
                if (c.getSuit() != firstSuit)
                    return false;
            }
            return true;
        }

        //check if an array contains distinct integer
        public static bool distinctInt(int[] intArray)
        {
            int[] disArray = intArray.Distinct().ToArray();
            int originalSize = intArray.Length;
            int distinctSize = disArray.Length;

            if (distinctSize < originalSize)
                return false;
            else
                return true;
        }

        //do the list of cards have consecutive rank
        public static bool ConsecutiveRank(IList<Card> lCard)
        {
            Rank[] rankArray = new Rank[lCard.Count];
            int i = 0;
            foreach (Card c in lCard)
            {
                rankArray[i] = c.getRank();
                i++;
            }
            //if the array is not distinct, return false
            if(rankArray.Distinct().Count() != rankArray.Count())
                return false;

            //no distinct value, need to check if max-min = size-1
            int minRank = (int)rankArray.Min();
            int maxRank = (int)rankArray.Max();

            //if max-min = size-1, return true
            if ((maxRank - minRank) == lCard.Count - 1)
                return true;
            else
                return false;
        }

        public bool CanAddACard(Card c)
        {
            IList<Card> tempListCard = this.Cards;
            tempListCard.Add(c);

            if (IsValid(tempListCard))
            {
                tempListCard.Remove(c);
                return true;
            }
            else
            {
                tempListCard.Remove(c);
                return false;
            }
        }

        public void AddACard(Card c)
        {
            if (IsValid(this.Cards.Concat(new [] {c}).ToList()))
            {
                this.Cards.Add(c);
            }
            else
            {
                throw new ArgumentException("Cannot add this card to existing meld", "c");
            }
        }

        public List<Card> getCards()
        {
            return _cards;
        }

        public string CardsToString()
        {
            string s = "";
            foreach (Card c in _cards)
                s += c.ToString() + " / ";
            return s;
        }

        public IEnumerator<Card> GetEnumerator()
        {
            return Cards.GetEnumerator();
        }

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return Cards.GetEnumerator();
        }
    }
}
