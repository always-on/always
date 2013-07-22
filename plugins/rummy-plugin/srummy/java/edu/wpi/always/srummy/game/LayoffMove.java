package edu.wpi.always.srummy.game;

/**
 * A class for Rummy game layoff move.
 * @author Morteza Behrooz
 * @version 1.0
 */

public class LayoffMove extends Move{
	
	private final Card playedCard;
	private final Meld relatedMeld;
	
	
	public Card getCard(){
		return playedCard;
	}

	public Meld getItsMeld(){
		return relatedMeld;
	}
	
	public LayoffMove(Player thePlayer, Card aCard, Meld aMeld){
		
		super(thePlayer);
		this.playedCard = aCard;
		this.relatedMeld = aMeld;

	}
	
	/**
	 * Checks if another LayoffMove is semantically 
	 * the same one by Card, Player and Meld criteria.
	 * @param Object someMove
	 * @return true if same Card, Player and Meld (semantically same Layoff).
	 * @see TestLayOffMove
	 */
	@Override
	public boolean equals(Object someMove){
		
		if(someMove == null 
				|| !(someMove instanceof LayoffMove))
			return false;
		
		LayoffMove supposedlySameLayOffMove 
				= (LayoffMove) someMove;
		
		if(!(supposedlySameLayOffMove.getPlayer()
				== this.player))
			return false;
		
		if(!supposedlySameLayOffMove.getCard()
				.equals(this.playedCard))
			return false;
		
		if(!supposedlySameLayOffMove.getItsMeld()
				.equals(this.playedCard))
			return false;
		
		return true;
	
	}
	
	/**
	 * Overides hashCode() of Object as equals() is overriden
	 * (In Java, hashCode() must return the same 
	 * value for two objects which are equal)
	 * @return int hashCode
	 * @see TestLayoffMove
	 */
	@Override
	public int hashCode(){
		
		return player.ordinal()
				+ 2 * playedCard.hashCode()
				+ 2 * relatedMeld.hashCode()
				;
		
	}
	
	/**
	 * States the layoff move as String. 
	 * @return String of layoff statement
	 */
	@Override
	public String toString(){
		
		return 
				this.player.name()
				+ " doing a layoff with adding "
				+ playedCard.toString()
				+ " to the "
				+ relatedMeld.toString()
		;
		
	}

	@Override
	public void happen(GameState gameState) {
		
		gameState.layOff(player, playedCard, relatedMeld);
		
	}
	

}
