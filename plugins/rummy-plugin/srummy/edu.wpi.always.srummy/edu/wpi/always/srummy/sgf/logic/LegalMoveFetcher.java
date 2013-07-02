package edu.wpi.always.srummy.sgf.logic;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.always.srummy.game.Move;
import edu.wpi.sgf.logic.GameLogicState;
import edu.wpi.sgf.logic.LegalMove;
import edu.wpi.sgf.logic.LegalMoveGenerator;



public class LegalMoveFetcher implements LegalMoveGenerator{

	public static ArrayList<Move> currentPossibleMoves = 
			new ArrayList<Move>();

	public void fetchMoves(List<Move> somePossibleMoves) {
		currentPossibleMoves.clear();

		if(somePossibleMoves != null)
			currentPossibleMoves.addAll(somePossibleMoves);

	}

	@Override
	public List<LegalMove> generate(GameLogicState state) {
		return null;
	}

}
