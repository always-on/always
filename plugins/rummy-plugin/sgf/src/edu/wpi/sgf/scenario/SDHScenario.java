package edu.wpi.sgf.scenario;

import java.util.Arrays;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * Generic SDH scenario class for
 * Social GamePlay Framework;
 * (Self deprecating humor)
 * @author Morteza Behrooz
 * @version 2.0
 * @see Scenario
 */

public class SDHScenario extends Scenario {

	private static final String 
	SelfDepractingHumorScenarioName = "sdh";

	double n = .5 , m = .3;
	int r = 2, s = 4;

	/**
	 * Some arguments can be added to the
	 * constructor as an argument for easy 
	 * adjustments and tunings.
	 */
	public SDHScenario(int t1, int t2, 
			double s1, double s2) {

		super(SelfDepractingHumorScenarioName,
				ScenarioType.generic);

		addSocialAttributes();

		//here you add the scenario to the system
		allScenarios.put(
				socialAttributes, SDHScenario.class);

		progress = 0;

		/*here you add the commenting tags you see 
		necassary for forcing to commenting system
		in scenario critical moments (progresses values).*/
		commentingProposals.put(1, 
				Arrays.asList("competition"));
		commentingProposals.put(2, 
				Arrays.asList("competition"));
		commentingProposals.put(3, 
				Arrays.asList("brag"));

	}

	/**
	 * Here, the developer can add 
	 * social attributes for a new 
	 * scenario.
	 * @see Scenario
	 */
	//TODO Can/should be moved to a JSON, or not?
	private void addSocialAttributes() {
		socialAttributes.add("humor");
		socialAttributes.add("laughter");
		socialAttributes.add("confidence");
		socialAttributes.add("amaze");
		socialAttributes.add("closeness");
	}

	@Override
	//See Javadoc for the parent class.
	public boolean evaluate(AnnotatedLegalMove move) {

		if(progress < r)
			if(move.getAnnotation() > n)
				return true;

		if(progress >= r)
			if(move.getAnnotation() < n)
				return true;

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wpi.sgf.scenario.Scenario#pickAmong(java.util.List)
	 * 
	 * This method picks 1 annotated legal move, among 
	 * already passed-evaluation annotated legal moves,
	 * as follows: It randomizes same-most-desired values,
	 * (equal maxes or equal mins, whethere we want max or min)
	 * and returns one move among them randomly.
	 * 
	 * LATER TO BE SEEN IF NECESSARY
	 */
	/*
	@Override
	public AnnotatedLegalMove pickAmong(
			List<AnnotatedLegalMove> fittingMoves) {

		//here has to sort ascending or descending based on desired move...
		Collections.sort(fittingMoves);
		List<AnnotatedLegalMove> maxminFittingMoves = 
				new ArrayList<AnnotatedLegalMove>();

			int i = 0; 
			double maxmin;
			maxminFittingMoves .add(fittingMoves.get(i));
			maxmin = fittingMoves.get(i).getAnnotation();
			while(i < fittingMoves.size()){
				if(fittingMoves.get(++i).getAnnotation() == maxmin)
					maxminFittingMoves.add(fittingMoves.get(i));
				else 
					break;
			}

		Collections.shuffle(maxminFittingMoves);
		return maxminFittingMoves.get(0);
	}
	 */

}
