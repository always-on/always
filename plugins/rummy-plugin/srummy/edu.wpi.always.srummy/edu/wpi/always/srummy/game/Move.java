package edu.wpi.always.srummy.game;

import edu.wpi.sgf.logic.LegalMove;

/**
 * An abstract class for Move.
 * @author Morteza Behrooz
 * @version 1.0
 */

public abstract class Move implements LegalMove {

	protected Player player;

	public Move(Player player){
		this.player = player;
	}

	public Player getPlayer(){
		return this.player;
	}

	public abstract void happen(
			GameState gameState);

}
