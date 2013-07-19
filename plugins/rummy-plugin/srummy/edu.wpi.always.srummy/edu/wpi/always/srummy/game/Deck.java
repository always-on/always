package edu.wpi.always.srummy.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A class for card deck.
 * @author Morteza Behrooz
 * @version 1.1
 */

public class Deck {

	private List<Card> deck;
	private int index;

	public Deck(){
		
		deck = new ArrayList<Card>();
		index = 0;
	
	}

	/**
	 * Adds a card to the deck.
	 * @param card card to be added to the deck.
	 */
	public void addCard(Card card){
	
		deck.add(card);
	
	}

	/**
	 * The size of a deck of cards.
	 * @return the number of cards present in the full deck.
	 */
	public int getSizeOfDeck(){
	
		return deck.size();
	
	}

	/**
	 * The number of cards left in the deck.
	 * @return the number of cards left to be dealt from deck.
	 */
	public int getNumberOfCardsRemaining() {
	
		return deck.size() - index;
	
	}

	/**
	 * Deal one card from the deck.
	 * @return a card from the deck, or null if empty.
	 */

	public Card dealCard() {
	
		if(index >= deck.size())
			return null;
		
		return deck.get(index++);
	
	}

	/**
	 * Shuffles the cards present in the deck.
	 */
	public void shuffleCards(){
	
		Collections.shuffle(deck);
	
	}

	/**
	 * Check to see if the deck still has cards for dealing.
	 * @return true if there are no cards left in deck, false otherwise.
	 */
	public boolean isEmpty(){
	
		if (index >= deck.size())
			return true;
		return false;

	}

	/**
	 * Restores the deck back to being full.
	 */
	public void restoreDeck() {
	
		index = 0;
	
	}  

	//SORT, POP, peek, peekAt, push, rank, 

	public void create(List<Card> except) {
		
		removeAll();
		insertDeckbySuit(except);
		Collections.shuffle(deck);
		
	}
	
	public void create() {
		
		removeAll();
		insertDeckbySuit();
		Collections.shuffle(deck);
		
	}

	private void insertDeckbySuit(List<Card> except) {

		for(Rank eachRank : Rank.values()){
			for(Suit eachSuit : Suit.values()){
				Card aCard = new Card(eachRank, eachSuit);
				if(!except.contains(aCard))
					addCard(aCard);
			}
		}
		
	}
	
	private void insertDeckbySuit() {

		for(Rank eachRank : Rank.values()){
			for(Suit eachSuit : Suit.values()){
				Card aCard = new Card(eachRank, eachSuit);
					addCard(aCard);
			}
		}
		
	}

	private void removeAll() {
		
		deck.clear();
		index = 0;
		
	}
	
	public void synch(List<Card> newCardsList, int newIndex){
		
		removeAll();
		deck.addAll(newCardsList);
		index = newIndex;
		
	}

	public Card pop() {
		
		int last = deck.size() - 1;
		Card popped = deck.get(last);
		deck.remove(last);
		return popped;
		
	}
	
	public int size(){
		
		return deck.size(); 
	
	}
	
}
