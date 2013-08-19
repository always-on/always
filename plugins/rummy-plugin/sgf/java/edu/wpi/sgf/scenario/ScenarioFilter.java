package edu.wpi.sgf.scenario;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * ScenarioFilter class for 
 * Social Gameplay Framework.
 * @author Morteza Behrooz
 * @version 2.0
 */

public class ScenarioFilter {

	/**
	 * Filters the input Annotated Legal Moves
	 * Based on the criteria of scenario instance 
	 * provided to it by using Scenario filter().
	 * @param a List of AnnotatedLegalMove, a Scenario
	 * @return a List of AnnotatedLegalMove which are 
	 * passed according to the scenario criteria.
	 * Returns empty list if the input list is empty or null;
	 * @see ScenarioManager
	 */
	public List<AnnotatedLegalMove> 
	filter(List<AnnotatedLegalMove> someMoves, 
			Scenario aScenario){

		List<AnnotatedLegalMove> passedMoves 
		= new ArrayList<AnnotatedLegalMove>();

		if(someMoves == null 
				|| someMoves.isEmpty())
			return passedMoves;

		for(AnnotatedLegalMove eachMove : someMoves)
			if(aScenario.evaluate(eachMove, true))
				passedMoves.add(eachMove);

		/*here, if no move are found suitable for the progress of 
		the current scenario, the failures will increase and if 
		necessary, the ScenarioManager will change the scenario.*/
		if(passedMoves.isEmpty())
			aScenario.incrementFailures();

		return passedMoves;

	}

	/**
	 * Filters input Annotated Legal Moves
	 * Based on the criteria of each scenario 
	 * instance currently active
	 * @since 2.0
	 * @parama List of AnnotatedLegalMove,
	 * @param a List of Scenario
	 * @return a List of AnnotatedLegalMove which are 
	 * passed according to the active scenarios criteria.
	 * Returns empty list if any of the input lists are
	 * empty or null;
	 */
	public List<AnnotatedLegalMove> 
	filter(List<AnnotatedLegalMove> someMoves, 
			List<Scenario> activeScenarios){


		List<AnnotatedLegalMove> passedMoves 
		= new ArrayList<AnnotatedLegalMove>();

		if(someMoves == null 
				|| someMoves.isEmpty())
			return passedMoves;


		if(activeScenarios == null 
				|| activeScenarios.isEmpty())
			return passedMoves;

		for(AnnotatedLegalMove eachMove : someMoves){
			for(Scenario eachActiveScenario : activeScenarios){
				if(!eachActiveScenario.evaluate(eachMove, true))
					break;
			}
			passedMoves.add(eachMove);
		}
		return passedMoves;

	}

}
