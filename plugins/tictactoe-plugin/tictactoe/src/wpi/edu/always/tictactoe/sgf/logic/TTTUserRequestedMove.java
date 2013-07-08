package wpi.edu.always.tictactoe.sgf.logic;

public class TTTUserRequestedMove {
	
	protected int cellNumber;
	
	public TTTUserRequestedMove(int cellNumber){
		this.cellNumber = cellNumber;
	}
	
	/**
	 * Returns an instance of TTTLegalMove 
	 * After being checked by the caller 
	 * that this move is legal according to 
	 * the game state.
	 * @return TTTLegalMove
	 */
	public TTTLegalMove confirm(){
		return new 
				TTTLegalMove(this.cellNumber);
	}
}
