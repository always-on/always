package edu.wpi.sgf.scenario;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * ScenarioFilter class for 
 * Social Gameplay Framework.
 * @author Morteza Behrooz
 * @version 1.0
 */

public class ScenarioFilter {
	
	/**
	 * Filters the input Annotated Legal Moves
	 * Based on the criteria of scenario instance 
	 * provided to it by using Scenario filter().
	 * 
	 * @param a List of AnnotatedLegalMove, a Scenario
	 * @return a List of AnnotatedLegalMove which are 
	 * passed according to the scenario criteria.
	 * 
	 * @author Morteza Behrooz
	 */
	public List<AnnotatedLegalMove> 
		filter(List<AnnotatedLegalMove> someMoves, 
				Scenario aScenario){
		
		List<AnnotatedLegalMove> passedMoves 
			= new ArrayList<AnnotatedLegalMove>();
		
		for(AnnotatedLegalMove eachMove : someMoves)
			if(aScenario.evaluate(eachMove))
				passedMoves.add(eachMove);
		
		return passedMoves;
	
	}
	
}
