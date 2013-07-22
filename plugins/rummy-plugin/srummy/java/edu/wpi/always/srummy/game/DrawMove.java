package edu.wpi.always.srummy.game;

/**
 * A class for Rummy game draw move.
 * @author Morteza Behrooz
 * @version 1.0
 */
public class DrawMove extends Move{

	private final Pile pile;
	
	public DrawMove(Player thePlayer, Pile aPile){
		
		super(thePlayer);
		pile = aPile;
		
	}
	
	public Pile getPile(){
		
		return pile;
		
	}

	/**
	 * Checks if another DrawMove is semantically 
	 * the same one by Player and Pile criteria.
	 * @param Object someMove
	 * @return true if same Player and Pile (semantically same Draw).
	 * @see TestDrawMove
	 */
	@Override
	public boolean equals(Object someMove){
		
		if(someMove == null 
				|| !(someMove instanceof DrawMove))
			return false;
		
		DrawMove supposedlySameDrawMove 
				= (DrawMove) someMove;
		
		if(!(supposedlySameDrawMove.getPlayer()
				== this.player))
			return false;
		
		if(supposedlySameDrawMove.getPile()
				!= this.pile)
			return false;
		
		return true;
	
	}
	
	/**
	 * Overides hashCode() of Object as equals() is overriden
	 * (In Java, hashCode() must return the same 
	 * value for two objects which are equal)
	 * @return int hashCode
	 * @see DrawMove
	 */
	@Override
	public int hashCode(){
		
		return player.ordinal()
				+ 2 * pile.ordinal()
				;
		
	}
	
	/**
	 * States the discard move as String. 
	 * @return String of discard statement
	 */
	@Override
	public String toString(){
		
		return 
				player.name()
				+ " just drew a card from "
				+ pile.toString()
		;
		
	}
	
	@Override
	public void happen(GameState gameState) {

		gameState.drawCard(player, pile);
		
	}
	
}
