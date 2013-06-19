package edu.wpi.always.srummy.game;

import java.util.Iterator;

/**
 * A class for playing card.
 * @author Morteza Behrooz
 * @version 1.2
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
	 * Constructor with int as rank (converts to Rank)
	 * @param int value of card, suit
	 */
	public Card(int rank, Suit suit){
		
		this(Rank.values()[rank - 1], suit);
		
	}
	
	public Rank getRank(){
		
		return this.rank;
		
	}
	
	public Suit getSuit(){
		
		return this.suit;
		
	}
	
	public boolean isFacedUp(){
		
		return this.facedUp;
		
	}

	public void faceItUp(){
		
		facedUp = true;
		
	}
	
	public void flipIt(){
		
		this.facedUp = !this.facedUp;
		
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
	
		return "The " + rank + " of " + suit; 

	}
	
	/**
	 * Checks if another Card is semantically the
	 * same Meld by checking the Suit and Rank.
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
				13 * suit.ordinal() 
				+ rank.ordinal()
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
