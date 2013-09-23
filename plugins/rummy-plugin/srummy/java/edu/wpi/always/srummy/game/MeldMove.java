package edu.wpi.always.srummy.game;

/**
 * A class for Rummy game meld move.
 * @author Morteza Behrooz
 * @version 1.0
 */

public class MeldMove extends SrummyLegalMove{
	
	private final Meld meld;

	public Meld getItsMeld(){
		
		return meld;
	
	}

	public MeldMove(Player thePlayer, Meld aMeld){
		
		super(thePlayer);
		meld = aMeld;
		
	}
	
	/**
	 * Checks if another MeldMove is semantically 
	 * the same one by Player and Meld criteria.
	 * @param Object someMove
	 * @return true if same Player and Meld (semantically same MeldMove).
	 * @see TestMeldMove
	 */
	@Override
	public boolean equals(Object someMove){
		
		if(someMove == null 
				|| ! (someMove instanceof MeldMove))
			return false;
		
		MeldMove supposedlySameMeldMove 
				= (MeldMove) someMove;
		
		if(!(supposedlySameMeldMove.getPlayer()
				== this.player))
			return false;
		
		if(!supposedlySameMeldMove.getItsMeld()
				.equals(this.meld))
			return false;
		
		return true;
		
	}
	
	/**
	 * Overides hashCode() of Object as equals() is overriden
	 * (In Java, hashCode() must return the same 
	 * value for two objects which are equal)
	 * @return int hashCode
	 * @see MeldMove
	 */
	@Override 
	public int hashCode(){
		
		return player.ordinal()
				+ 2 * meld.hashCode()
				;
		
	}
	
	/**
	 * States the meld move as String. 
	 * @return String of meld move statement
	 */
	@Override
	public String toString(){
		
		return 
				this.player.name()
				+ " melding "
				+ meld.toString()
		;
		
	}
	
	@Override
	public void happen(SrummyGameState gameState) {
		
		gameState.meld(player, meld);
		
	}

		
}
