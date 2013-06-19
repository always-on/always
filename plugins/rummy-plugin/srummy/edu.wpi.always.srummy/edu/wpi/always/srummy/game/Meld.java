package edu.wpi.always.srummy.game;

import java.util.ArrayList;
import java.util.List;


/**
 * A class for simulating rummy meld.
 * @author Morteza Behrooz
 * @version 1.0
 */

public class Meld implements Iterable<Card>{

	private List<Card> meldCards = new ArrayList<Card>();

	/**
	 * Constructor for a meld (run)
	 * @param Suit of meld, startRank as an int, number of cards in the meld
	 */
	public Meld(Suit suit, int startRank, int count) throws Exception{

		if(count <= 2)
			throw new IllegalArgumentException(
					"Minimum number of cards for a meld is 2.");

		for(int i = startRank; i < startRank + count; i++)
			meldCards.add(new Card(i, suit));

	}

	/**
	 * Constructor for a meld (run)
	 * @param Suit of meld, startRank as a Rank, number of cards in the meld
	 */
	public Meld(Suit suit, Rank startRank, int count) throws Exception{

		this(suit, startRank.ordinal() + 1, count);

	}

	/**
	 * Constructor for a meld (set/group)
	 * @param Suits of meld as a list, rank as an int
	 */
	public Meld(List<Suit> suits, int rank) throws Exception{

		if(suits.size() <= 2)
			throw new IllegalArgumentException(
					"Minimum number of cards for a meld is 2.");

		for(Suit suit : suits)
			meldCards.add(new Card(rank, suit));

	}

	/**
	 * Constructor for a meld (set/group)
	 * @param Suits of meld as a list, rank as a Rank
	 */
	public Meld(List<Suit> suits, Rank rank) throws Exception{

		this(suits, rank.ordinal() + 1);

	}

	public Meld(List<Card> someCards) {
		
		if(isValid(someCards))
			for(Card eachCard : someCards)
				meldCards.add(eachCard);
		
		else
			throw new IllegalArgumentException(
					"Cannot meld from these cards.");
			
	}

	/**
	 * Constructor for a meld
	 */
	public Meld() {

	}

	/**
	 * @return meld cards
	 */
	List<Card> getCards(){
		return meldCards; 
	}


	/**
	 * Are some Cards have a valid meld
	 * @param a List of Card
	 * @return true if a valid meld, false otherwise.
	 */
	public boolean isValid(List<Card> someCards){
		if(someCards.size() < 3)
			return false;
		return isValidSubset(someCards);
	}

	/**
	 * Do some Cards have a valid property for a meld
	 * @param a List of Card
	 * @return true if valid meld property, false otherwise.
	 */
	private boolean isValidSubset(List<Card> someCards){

		if(haveSameRank(someCards))
			return true;
		if(haveSameSuit(someCards) && haveConsecutiveRank(someCards))
			return true;
		return false;

	}

	/**
	 * Do some Cards have same Suit
	 * @param a List of Card
	 * @return true if all cards have same Suit, false otherwise.
	 */
	private boolean haveSameSuit(List<Card> someCards){

		Suit firstSuit = someCards.get(0).getSuit();
		for(Card oneOfThem : someCards)
			if(!oneOfThem.getSuit().equals(firstSuit))
				return false;
		return true;

	}

	/**
	 * Do some Cards have same Rank
	 * @param a List of Card
	 * @return true if all cards have same Rank, false otherwise.
	 */
	private boolean haveSameRank(List<Card> someCards){

		Rank firstRank = someCards.get(0).getRank();
		for(Card eachOfThem : someCards)
			if(!eachOfThem.getRank().equals(firstRank))
				return false;
		return true;

	}

	/**
	 * Do some cards have consecutive Rank
	 * @param a List of Card
	 * @return true if all cards have consecutive Rank, false otherwise.
	 * @see TestMeld
	 */
	private boolean haveConsecutiveRank(List<Card> someCards){

		Rank firstRank = someCards.get(0).getRank();
		for(int index = 1; index < someCards.size(); index++)
			if(someCards.get(index).getRank().ordinal() 
					!= firstRank.ordinal() + index 
					|| (someCards.get(index).getRank().ordinal() == 11 
					&& index != someCards.size()))
				return false;
		return true;

	}

	/**
	 * Can a Card be a valid addition to the current Meld
	 * @param a Card
	 * @return true if a valid addition for the Meld, false otherwise.
	 * @see TestMeld
	 */
	public boolean canAlsoHaveThis(Card cardToBeAdded){
		
		List<Card> tempMeldCards = this.meldCards;
		tempMeldCards.add(cardToBeAdded);
		
		if(isValid(tempMeldCards)){
			tempMeldCards.remove(cardToBeAdded);
			return true;
		}
		else{
			tempMeldCards.remove(cardToBeAdded);
			return false;
		}
		
	}
	
	/**
	 * Adds a Card to a current Meld if a valid addition
	 * @param a Card
	 * @see TestMeld
	 */
	public void addThis(Card addingCard){
		
		if(!canAlsoHaveThis(addingCard))
			throw new IllegalArgumentException(
					"This card cannot be addded to this meld");
		
		this.meldCards.add(addingCard);
	
	}
	
	/**
	 * Checks if another Meld is semantically the
	 * same Meld by checking the actual each Card.
	 * @param Object someMeld
	 * @return true if same Meld, Player and Meld (semantically same Meld).
	 * @see TestMeld
	 */
	@Override
	public boolean equals(Object someMeld){
		
		if(someMeld == null 
				|| !(someMeld instanceof Meld))
			return false;
		
		Meld supposedlySameMeld = (Meld) someMeld;
		
		if(supposedlySameMeld.getCards().size()
				!= this.meldCards.size())
			return false;
		
		for(Card eachCard : this.meldCards)
			if(!supposedlySameMeld.getCards()
					.contains(eachCard))
				return false;
		
		return true;

	}
	
	/**
	 * Overides hashCode() of Object as equals() is overriden
	 * (In Java, hashCode() must return the same 
	 * value for two objects which are equal)
	 * @return int hashCode
	 * @see TestMeld
	 */
	@Override
	public int hashCode(){
		
		return 
				meldCards.get(0).hashCode()
				+ 100 * meldCards.get(1).hashCode()
				+ 10000 * meldCards.get(2).hashCode()
				;
		
	}

	@Override
	public java.util.Iterator<Card> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
