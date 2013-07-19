package edu.wpi.always.srummy.game;

import java.util.Iterator;

/**
 * A class for playing card.
 * @author Morteza Behrooz
 * @version 1.3
 */

public class Card implements Iterable<Card>{

	private final Suit suit;
	private final Rank rank;

	private boolean facedUp = true;

	/**
	 * Constructor with Rank as rank
	 * @param int value of card, suit
	 */
	public Card(Rank rank, Suit suit){

		this.rank = rank;
		this.suit = suit;

	}

	/**
	 * Constructor with an Integer as the rank
	 * @param int value of card, suit
	 */
	public Card(int rank, Suit suit){

		this(Rank.values()[rank - 1], suit);

	}

	/**
	 * Constructor for Card class using an abbreviation of the card name as input.
	 * Assumed format: For the Suit, as the first character, first char of
	 * suit name, for the Rank, as the second character, the first character 
	 * of the rank if Ace, Jack, Queen or King, otherwise the number.
	 * Example: D4, HA, HJ, H8
	 * @param String abbreviation
	 */
	public Card (String abbrevation){

		abbrevation = abbrevation.toLowerCase().trim();
		Suit aSuit = null;
		if(abbrevation.startsWith("c"))
			aSuit = Suit.Clubs;
		else if(abbrevation.startsWith("d"))
			aSuit = Suit.Diamonds;
		else if(abbrevation.startsWith("h"))
			aSuit = Suit.Hearts;
		else if(abbrevation.startsWith("s"))
			aSuit = Suit.Spades;

		Rank aRank = null;
		if(abbrevation.endsWith("a"))
			aRank = Rank.Ace;
		else if(abbrevation.endsWith("2"))
			aRank = Rank.Two;
		else if(abbrevation.endsWith("3"))
			aRank = Rank.Three;
		else if(abbrevation.endsWith("4"))
			aRank = Rank.Four;
		else if(abbrevation.endsWith("5"))
			aRank = Rank.Five;
		else if(abbrevation.endsWith("6"))
			aRank = Rank.Six;
		else if(abbrevation.endsWith("7"))
			aRank = Rank.Seven;
		else if(abbrevation.endsWith("8"))
			aRank = Rank.Eight;
		else if(abbrevation.endsWith("9"))
			aRank = Rank.Nine;
		else if(abbrevation.endsWith("10"))
			aRank = Rank.Ten;
		else if(abbrevation.endsWith("j"))
			aRank = Rank.Jack;
		else if(abbrevation.endsWith("q"))
			aRank = Rank.Queen;
		else if(abbrevation.endsWith("k"))
			aRank = Rank.King;
		
		suit = aSuit;
		rank = aRank;
		
	}


	public Rank getRank(){

		return rank;

	}

	public Suit getSuit(){

		return suit;

	}

	public boolean isFacedUp(){

		return facedUp;

	}

	public void faceItUp(){

		facedUp = true;

	}

	public void flipIt(){

		facedUp = !facedUp;

	}

	public boolean hasTheSameSuitAs(Card anotherCard){

		if(anotherCard.equals(null)) 
			return false;
		return (this.suit == anotherCard.suit);

	}

	public boolean hasTheSameRankAs(Card anotherCard){

		if(anotherCard.equals(null)) 
			return false;
		return (this.rank == anotherCard.rank);

	}

	public String toString(){ 

		return rank + " of " + suit; 

	}

	/**
	 * Checks if another Card is semantically the
	 * same Card by checking the Suit and Rank.
	 * @param Object someCard
	 * @return true if same Suit and Rank (semantically same Card).
	 * @see TestCard
	 */
	@Override
	public boolean equals(Object someCard){

		if(someCard == null 
				|| !(someCard instanceof Card))
			return false;

		Card supposedlySameCard = (Card) someCard;

		return 
				(supposedlySameCard.getSuit() 
						== this.suit 
						&& supposedlySameCard.getRank() 
						== this.rank)
						;

	}

	/**
	 * Overides hashCode() of Object as equals() is overriden
	 * (In Java, hashCode() must return the same 
	 * value for two objects which are equal)
	 * @return int hashCode
	 * @see TestCard
	 */
	@Override
	public int hashCode(){

		return 
				13 * (suit.ordinal() + 1) 
				+ rank.ordinal() + 1
				;

	}

	/**
	 * Following method returns the comparison 
	 * value after comparing two cards.
	 * @param card to be compared
	 * @return negative int if the current 
	 * card is smaller, positive else.
	 * @see TestCard
	 */
	public int compareTo(Card anotherCard) {
		int rankComparison = this.rank.compareTo(anotherCard.rank);
		return rankComparison != 0 ? rankComparison 
				: this.suit.compareTo(anotherCard.suit);
	}

	@Override
	public Iterator<Card> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
