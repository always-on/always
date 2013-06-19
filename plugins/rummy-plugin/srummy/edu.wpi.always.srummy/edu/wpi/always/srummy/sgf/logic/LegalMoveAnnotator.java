package edu.wpi.always.srummy.sgf.logic;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.always.srummy.game.Move;
import edu.wpi.sgf.logic.AnnotatedLegalMove;

public class LegalMoveAnnotator 
	implements edu.wpi.sgf.logic.LegalMoveAnnotator{
	
	public List<AnnotatedLegalMove> annotate(List<Move> someMoves){
		
		List<AnnotatedLegalMove> annotatedMoves = new ArrayList<AnnotatedLegalMove>();
		for(Move eachMove : someMoves)
			annotatedMoves.add(new AnnotatedLegalMove(eachMove, 0.9));
		
		
		return annotatedMoves;
		
	}
	
	
	
}
