package edu.wpi.sgf.scenario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.sgf.logic.AnnotatedLegalMove;

/**
 * Scenario abstract class for Social GamePlay Framework;
 * Other Scenarios derive from this class.
 * {@link #allScenarios} is a static Map which allows
 * developers to add their new scenarios to 
 * the system along with their social attributes
 * @author Morteza Behrooz
 * @version 2.1
 */

public abstract class Scenario {
	
	private static final int scenarioFailingBearLimit = 1;
	protected int progress;
	
	/*this var shows the max number of failures
	allowed for a scenario to bear before 
	avoided being followed*/
	protected int failingBear = 
			scenarioFailingBearLimit;
	
	private int failures;
	
	protected final ScenarioType type;
	
	protected final String name;
	
	protected Map<Integer, List<String>>
		commentingProposals;

	public static Map<List<String>,
		Class<? extends Scenario>> allScenarios 
			= new HashMap<List<String>, Class<? 
					extends Scenario>>();
	
	public ScenarioType getType(){

		return type;
	
	}
	
	public Scenario(String name, ScenarioType type){
		
		commentingProposals = 
				new HashMap<Integer, List<String>>();
		
		progress = 0;
		failures = 0;
		this.name = name;
		this.type = type;
		
	}
	
	
	public int tellProgress(){
		return progress;
	}
	
	public String getName(){
		return name;
	}
	
	public int getFailures(){
		return failures;
	}
	
	public void incrementFailures(){
		failures++;
	}
	
	/**
	 * This abstract method will make the scenario
	 * progress of 1 move in the time.
	 * (Time is represented by agent moves
	 * in this framework.)
	 */
	public void tick(){
		
		this.progress ++;
		
	}
	
	/**
	 * This abstract method will report back
	 * the mooments which have tags 
	 * for special comments and the
	 * tags specified for them. 
	 * @return List <String> of comment tags 
	 * for the current moment came from scenario.
	 */
	public List<String>
	reportCommentingTagsForTheCurrentProgress(
			int questionedProgress) {

		if(commentingProposals
				.containsKey(questionedProgress))
			return commentingProposals
					.get(questionedProgress);
		return null;
		
	}
	
	/**
	 * Abstract evaluate() method will report back 
	 * if a given Annotated Legal Move
	 * has desired annotations for the 
	 * current progress of the scneario
	 * in the time.
	 * @param an object of AnnotatedLegalMove
	 * @return true if the annotated move 
	 * is within an accepted range for the 
	 * current progress of the scenario,
	 * false otherwise.
	 * @see AnnotatedLegalMove
	 */
	public abstract boolean evaluate(AnnotatedLegalMove move, boolean passAll);

}
