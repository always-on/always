package edu.wpi.always.srummy.game;

/**
 * A class for Rummy game discard move.
 * @author Morteza Behrooz
 * @version 1.0
 */

public class DiscardMove extends SrummyLegalMove{

	private final Card playedCard;
	
	public DiscardMove(Player thePlayer, Card aCard){
		
		super(thePlayer);
		playedCard = aCard;
		
	}
	
	public Card getCard(){
		
		return playedCard;
	
	}
	
	/**
	 * Checks if another DiscardMove is semantically 
	 * the same one by Card and Player criteria.
	 * @param Object someMove
	 * @return true if same Card and Player (semantically same Discard).
	 * @see TestLayOffMove
	 */
	@Override
	public boolean equals(Object someMove){
		
		if(someMove == null 
				|| !(someMove instanceof DiscardMove))
			return false;
		
		DiscardMove supposedlySameDiscardMove = 
				(DiscardMove) someMove;
		
		if(supposedlySameDiscardMove.getPlayer()
				!= this.player)
			return false;
		
		if(!supposedlySameDiscardMove.getCard()
				.equals(this.playedCard))
			return false;
		
		return true;
			
	}
	
	/**
	 * Overrides hashCode() of Object as equals() is 
	 * overridden (In Java, hashCode() must return the  
	 * same value for two objects which are equal)
	 * @return int hashCode
	 * @see TestDiscardMove
	 */
	@Override
	public int hashCode(){
		
		return player.ordinal()
				+ 2 * playedCard.hashCode()
				;
		
	}
	
	/**
	 * States the discard move as String. 
	 * @return String of discard statement
	 */
	@Override
	public String toString(){
		
		return 
				this.player.name()
				+ " discarding "
				+ this.playedCard.toString()
		;
		
	}
	
	@Override
	public void happen(SrummyGameState gameState){
		
		gameState.discardCard(player, playedCard);
		
	}


}
