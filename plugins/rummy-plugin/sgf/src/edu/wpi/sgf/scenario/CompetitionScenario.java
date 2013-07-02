//package edu.wpi.sgf.scenario;
//
//import edu.wpi.sgf.logic.AnnotatedLegalMove;
//
//public class CompetitionScenario extends Scenario {
//
//	private static final String 
//	SelfDepractingHumorScenarioName = "competition";
//
//	double n = .5;
//
//	/**
//	 * Some values can be added to the
//	 * constructor as an argument for easy 
//	 * adjustments and tunings.
//	 */
//	public CompetitionScenario(){
//		super(SelfDepractingHumorScenarioName,
//				ScenarioType.generic);
//
//		addSocialAttributes();
//
//		//here you add the scenario to the system
//		allScenarios.put(
//				socialAttributes, CompetitionScenario.class);
//
//		progress = 0;
//
//		/*here you add the commenting tags you see 
//		necassary for forcing to commenting system
//		in scenario critical moments (progresses values).*/
//		//> none for this scenario.
//
//	}
//
//	/**
//	 * Here, the developer can add 
//	 * social attributes for a new 
//	 * scenario.
//	 * @see Scenario
//	 */
//	//TODO Can/should be moved to a JSON, or not?
//	private void addSocialAttributes() {
//		socialAttributes.add("humor");
//		socialAttributes.add("laughter");
//		socialAttributes.add("confidence");
//		socialAttributes.add("amaze");
//		socialAttributes.add("closeness");
//	}
//
//	@Override
//	//See the Javadoc for the parent class.
//	public boolean evaluate(AnnotatedLegalMove move) {
//		
//		if(move.getAnnotation() >= n)
//			return true;
//		return false;
//		
//	}
//
//}
