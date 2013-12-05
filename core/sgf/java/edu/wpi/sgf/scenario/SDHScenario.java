package edu.wpi.sgf.scenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * Generic SDH scenario class for
 * Social GamePlay Framework;
 * (Self deprecating humor)
 * @author Morteza Behrooz
 * @version 2.1
 * @see Scenario
 */

public class SDHScenario extends Scenario {

	private static final String 
		SelfDepractingHumorScenarioName = "sdh";
	private static final int 
		WeakPlayStartTime = 2,
		WeakPlayDeadlineTime = 4;
	private static final double 
		StrongPlayStrengthLowerBound = .5, 
		WeakPlayStrengthUpperBound = .3;

	double strengthLowerBound, 
		strengthUpperBoaund;
	int t1, t2;
	
	protected static List<String> socialAttributes;

	//static block called by the ScenarioManager
	//to load scenarios into Scenario.allScenarios
	static{

		socialAttributes = 
				new ArrayList<String>();

		addSocialAttributes();
		//here you add the scenario to the system
		Scenario.allScenarios.put(
				socialAttributes, SDHScenario.class);

	}
	
	/**
	 * Constructor with constants as arguments.
	 * (for easy adjustments and tunings.)
	 */
	public SDHScenario(int t1, int t2, 
			double s1, double s2) {

		super(SelfDepractingHumorScenarioName,
				ScenarioType.generic);

		/*here you add the commenting tags you see 
		necessary for forcing to commenting system
		in scenario critical moments (progresses values).*/
		addCommentingTagProposals();

		strengthLowerBound = s1;
		strengthUpperBoaund = s2;
		this.t1 = t1; this.t2 = t2;

	}

	/**
	 * Constructor with nor arguments, 
	 * default constants are used.
	 */
	public SDHScenario(){
		
		super(SelfDepractingHumorScenarioName,
				ScenarioType.generic);
		
		//here you add the scenario to the system
		Scenario.allScenarios.put(
				socialAttributes, SDHScenario.class);

		addCommentingTagProposals();

		addDefaultValuesIfNotProvidedByConstrucot();

	}

	/**
	 * Here you can add the comment tag proposals which 
	 * scenario makes along with their times as map key set.
	 * @see Scenario
	 */
	//TODO read by file? import? add to xml?
	private void addCommentingTagProposals() {
		commentingProposals.put(1, 
				Arrays.asList("competition"));
		commentingProposals.put(2, 
				Arrays.asList("competition"));
		commentingProposals.put(3, 
				Arrays.asList("brag"));
	}
	
	/**
	 * Default set of constants if not provided by the constructor.
	 */
	private void addDefaultValuesIfNotProvidedByConstrucot(){

		t1 = WeakPlayStartTime;
		t2 = WeakPlayDeadlineTime;
		strengthLowerBound = StrongPlayStrengthLowerBound;
		strengthUpperBoaund = WeakPlayStrengthUpperBound;

	}

	/**
	 * Here, the developer can add 
	 * social attributes for a new 
	 * scenario.
	 * @see Scenario
	 */
	//TODO Can/should be moved to a JSON, or not?
	private static void addSocialAttributes() {
		socialAttributes.add("humor");
		socialAttributes.add("laughter");
		socialAttributes.add("confidence");
		socialAttributes.add("amaze");
		socialAttributes.add("closeness");
	}
	
	public List<String> getAttributess(){
		return socialAttributes;
	}
	
	public void setSocialAttirubtes(
			List<String> someAttributes){
		
		socialAttributes
			.addAll(someAttributes);
		
	}
	

	@Override
	//See Java-doc for the parent class.
	public boolean evaluate(AnnotatedLegalMove move, boolean passAll) {

		if(passAll)
			return true;

		if(progress < t1)
			if(move.getAnnotation() > strengthLowerBound)
				return true;

		if(progress >= t1)
			if(move.getAnnotation() < strengthLowerBound)
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
