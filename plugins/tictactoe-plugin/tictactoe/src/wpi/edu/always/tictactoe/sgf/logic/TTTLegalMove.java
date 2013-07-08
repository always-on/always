package wpi.edu.always.tictactoe.sgf.logic;

import edu.wpi.sgf.logic.LegalMove;

public class TTTLegalMove implements LegalMove{
	
	int cellNumber;
	
	public TTTLegalMove(int cellNumber){
		this.cellNumber = cellNumber;
	}
	
	public int getCellNum(){
		return cellNumber;
	}
	
}
