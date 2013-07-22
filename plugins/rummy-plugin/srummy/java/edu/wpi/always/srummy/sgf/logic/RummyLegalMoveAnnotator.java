package edu.wpi.always.srummy.sgf.logic;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.always.srummy.game.DiscardMove;
import edu.wpi.always.srummy.game.LayoffMove;
import edu.wpi.always.srummy.game.MeldMove;
import edu.wpi.always.srummy.game.Move;
import edu.wpi.sgf.logic.AnnotatedLegalMove;

public class RummyLegalMoveAnnotator 
	implements edu.wpi.sgf.logic.LegalMoveAnnotator{
	
	private static final double meldMoveStrength = 0.8;
	private static final double layoffMoveStrength = 0.6;
	private static final double discardMoveStrength = 0.4;
	
	
	public List<AnnotatedLegalMove> annotate(List<Move> someMoves){
		
		List<AnnotatedLegalMove> annotatedMoves = 
				new ArrayList<AnnotatedLegalMove>();
		
		if(someMoves == null 
				|| someMoves.isEmpty())
			return annotatedMoves;
		
		for(Move eachMove : someMoves){
			
			if(eachMove instanceof MeldMove)
				annotatedMoves.add(
						new AnnotatedLegalMove(
								eachMove, meldMoveStrength));
			else if (eachMove instanceof LayoffMove)
				annotatedMoves.add(
						new AnnotatedLegalMove(
								eachMove, layoffMoveStrength));
			else if (eachMove instanceof  DiscardMove)
				annotatedMoves.add(
						new AnnotatedLegalMove(
								eachMove, discardMoveStrength));
		
		}
		
		return annotatedMoves;
		
	}
	
	//for testing
	public String toString(List<AnnotatedLegalMove> someMoves){
		
		String desciption = "";
		for(AnnotatedLegalMove eachMove : someMoves)
			desciption +=
					((edu.wpi.always.srummy.game.Move)eachMove
					.getMove()).toString() + " >> " + 
					eachMove.getAnnotation() + "\n" 
					;
		return desciption;
			
	}
	
}
