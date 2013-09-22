package edu.wpi.always.srummy.game;

import edu.wpi.sgf.logic.LegalMove;

/**
 * An abstract class for Move.
 * @author Morteza Behrooz
 * @version 1.0
 */

public abstract class SrummyLegalMove implements LegalMove {

	protected Player player;

	public SrummyLegalMove(Player player){
		this.player = player;
	}

	public Player getPlayer(){
		return this.player;
	}
	
	public abstract void happen(
			SrummyGameState gameState);

}
