package edu.wpi.always.srummy.tests;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.always.srummy.game.Card;
import edu.wpi.always.srummy.game.Rank;
import edu.wpi.always.srummy.game.Suit;

/**
 * testing Card methods.
 * @see Card
 * @author Morteza Behrooz
 * @version 1.0
 */

public class TestCard {

	@Test
	public void test(){
	   
		Card aceOfSpades = new Card(Rank.Three, Suit.Spades);
	    Card fourOfClubs = new Card(Rank.Four, Suit.Clubs);
	    assertTrue(aceOfSpades.compareTo(fourOfClubs) < 0);
	
	}

}
