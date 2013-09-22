package edu.wpi.always.srummy.logic;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.always.srummy.game.SrummyLegalMove;
import edu.wpi.sgf.logic.GameLogicState;
import edu.wpi.sgf.logic.LegalMove;
import edu.wpi.sgf.logic.LegalMoveGenerator;

public class SrummyLegalMoveFetcher implements LegalMoveGenerator{

	public static List<LegalMove> currentPossibleMoves = 
			new ArrayList<LegalMove>();

	public void fetch(List<SrummyLegalMove> possibleMoves) {
		currentPossibleMoves.clear();

		if(possibleMoves != null)
			currentPossibleMoves.addAll(possibleMoves);

	}

	public List<LegalMove> getCurrentPossibleMoves(){
	   return currentPossibleMoves; 
	}
	
	@Override
	public List<LegalMove> generate(GameLogicState state) {
		return null;
	}

}
