package edu.wpi.always.srummy.sgf.logic;

import java.util.List;

import edu.wpi.sgf.logic.GameLogicState;
import edu.wpi.sgf.logic.LegalMove;
import edu.wpi.sgf.logic.LegalMoveGenerator;



public class LegalMoveFetcher implements LegalMoveGenerator{

	@Override
	public List<LegalMove> generate(GameLogicState state) {
		
		return fetchMoves();
	}

	private List<LegalMove> fetchMoves() {
		rummyPlugin.getMoves();
		return null;
	}

}
