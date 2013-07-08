package edu.wpi.sgf.comment;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.speech.AudioException;
import javax.speech.EngineException;
import javax.speech.EngineStateError;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.GameLogicState;
import edu.wpi.sgf.scenario.Scenario;
import edu.wpi.sgf.scenario.ScenarioManager;

public class CommentingManager {

	Tts tts; //Text to Speech (freetts)
	Gtts gtts; //Text to Speech (google (online))
	private CommentLibraryHandler libHandler;
	private List<Comment> eligibleComments;
	private List<String> currentMomentsTags;
	private Map<Comment, Integer> eachCommentsTagCovering;
	private Map<Comment, Integer> sortedEachCommentsTagCovering;
	//private Map<Comment, Integer> maxTagCoveringComments;
	private Map<Comment, Integer> shuffledMaxTagCoveringComments;

	/*all comments imported from edu.wpi.sgf.comment library 
	for making 'on' human moves or agent moves 'by' the agent*/
	private List<Comment> allComments = new ArrayList<Comment>();

	public CommentingManager(){

		libHandler = new CommentLibraryHandler();
		eligibleComments = new ArrayList<Comment>();
		currentMomentsTags = new ArrayList<String>();
		eachCommentsTagCovering = new HashMap<Comment, Integer>();
		sortedEachCommentsTagCovering = new TreeMap<Comment, Integer>();
		//maxTagCoveringComments = new HashMap<Comment, Integer>();
		shuffledMaxTagCoveringComments = new HashMap<Comment, Integer>();

		//get all comments from library
		allComments.addAll(
				libHandler.retrieveAllCommentsFromLibrary());

		gtts = new Gtts();
		tts = new Tts();
		try {
			tts.init("kevin16");
		} catch (EngineException | AudioException | 
				EngineStateError| PropertyVetoException e) {
			e.printStackTrace();
		}

	}

	public Comment pickCommentOnUserMove(GameLogicState gameState
			, AnnotatedLegalMove move, int who){

		eligibleComments.clear();
		currentMomentsTags.clear();

		/* the strategy to choose a comment for agent to make on human move is as follows:
		 * if win, lose or tie, respected tagged comments
		 * else, based on user's move strength...
		 */

		//win, lose, tie
		if(gameState.agentWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("agentWon"))
					eligibleComments.add(cm);
		if(gameState.userWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("userWins"))
					eligibleComments.add(cm);
		if(gameState.tie)
			for(Comment cm : allComments)
				if(cm.getTags().contains("tie"))
					eligibleComments.add(cm);

		for(Comment cm : allComments)
			if(cm.getStrengthLowerBount() < move.getMoveStrength()
					&&cm.getStrengthUpperBound() > move.getMoveStrength())
				eligibleComments.add(cm);


		return null;
	}

	public Comment pickCommentOnOwnMove(AnnotatedLegalMove agentMove){
		//GameLogicState gameState,, Scenario currentScenario
		eligibleComments.clear();
		currentMomentsTags.clear();
		eachCommentsTagCovering.clear();
		sortedEachCommentsTagCovering.clear();
		shuffledMaxTagCoveringComments.clear();

		//		if(gameState.agentWins)
		//			for(Comment cm : allComments)
		//				if(cm.getTags().contains("agentWon"))
		//					eligibleComments.add(cm);
		//		if(gameState.userWins)
		//			for(Comment cm : allComments)
		//				if(cm.getTags().contains("userWins"))
		//					eligibleComments.add(cm);

		for(Comment cm : allComments){

			if(agentMove.getAnnotation() < 0.5){
				for(String eachTag : cm.getTags())
					if(eachTag.contains("discard"))
						eligibleComments.add(cm);
			}
			if(0.4 < agentMove.getAnnotation() 
					&& agentMove.getAnnotation() < 0.7){
				for(String eachTag : cm.getTags())
					if(eachTag.contains("layoff"))
						eligibleComments.add(cm);
			}
			if(0.7 < agentMove.getAnnotation() 
					&& agentMove.getAnnotation() < 0.9){
				for(String eachTag : cm.getTags())
					if(eachTag.contains("meld"))
						eligibleComments.add(cm);
			}

		}

		if(eligibleComments.size() > 0)
			return eligibleComments.get(0);
		else return null;
	}
	/*This function chooses the edu.wpi.sgf.comment to be made by agent for an agent move
	 * who: game turn. 1: user, 2:agent*/ 
	public Comment pickCommentOnOwnMove(GameLogicState gameState, 
			List<Scenario> activeScenarios, AnnotatedLegalMove move, int who){

		eligibleComments.clear();
		currentMomentsTags.clear();
		eachCommentsTagCovering.clear();
		sortedEachCommentsTagCovering.clear();
		shuffledMaxTagCoveringComments.clear();

		/*the strategy to choose a edu.wpi.sgf.comment for agent to make on its own move is as follows:
		 * 
		 * -If no win, lose or tie AND no moment tags, no edu.wpi.sgf.comment is made. (LATER MAKE RANDOMLY MADE)
		 * -At either win, lose or tie situations, all appropriate comments are selected, one is
		 * chosen randomly.
		 * -Else, based on tags, all matching comments are chosen (w/ max # of covering tags), then 
		 * one is chosen between them based on prioritized order in the file or random. THINK LATER
		 */

		//win, lose, tie if applicable
		if(gameState.agentWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("agentWon"))
					eligibleComments.add(cm);
		if(gameState.userWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("userWins"))
					eligibleComments.add(cm);
		if(gameState.tie)
			for(Comment cm : allComments)
				if(cm.getTags().contains("tie"))
					eligibleComments.add(cm);

		//TEMP>> MAKE FLAG FOR NO SCENARIO TO SKIP THIS
		//moments 
//		for(Scenario easchScenario : activeScenarios)
//			if(easchScenario.
//					reportCommentingTagsForTheCurrentProgress(
//							easchScenario.tellProgress()) != null)
//				currentMomentsTags.addAll(easchScenario
//						.reportCommentingTagsForTheCurrentProgress(
//								easchScenario.tellProgress()));

		//to do max covering problem, then randomly or chosen with priority ...
		if(!currentMomentsTags.isEmpty()){
			int covering;
			boolean irrelevant;
			for(Comment cm : allComments){
				covering = 0;
				irrelevant = false;
				for(String eachTag : cm.getTags())
					if(currentMomentsTags.contains(eachTag))
						covering++;
				for(String eachTag : cm.getTags()) //TEMP? Comment tag defined consistent or what?
					if(!currentMomentsTags.contains(eachTag)
							&& !eachTag.contains("usr")
							&& !eachTag.contains("own"))
						irrelevant = true;
				if(cm.who == who && !irrelevant){
					eachCommentsTagCovering.put(cm, covering);
					eligibleComments.add(cm); //TEMP
				}
			}

			//			//sorting map by Google guava >> RETURNS ERROR, INVESTIGATE
			//			Ordering<Comment> valueComparator = Ordering.natural()
			//					.onResultOf(Functions.forMap(eachCommentsTagCovering)).reverse();
			//			sortedEachCommentsTagCovering = 
			//					ImmutableSortedMap.copyOf(eachCommentsTagCovering, valueComparator);


			//building a map that has only max covering comments.
			//			int maxCover = 0;
			//			boolean firstIteration = true;
			//			for(Map.Entry<Comment, Integer> each : sortedEachCommentsTagCovering.entrySet()){
			//				if(firstIteration){
			//					maxCover = each.getValue();
			//					maxTagCoveringComments.put(each.getKey(), each.getValue());
			//					firstIteration = false;
			//				}
			//					if(each.getValue() == maxCover)
			//						maxTagCoveringComments.put(each.getKey(), each.getValue());
			//					else
			//						break;
			//			}

			//			//shuffle it //(prioritize?)
			//			List<Comment> keys2shuffle = 
			//					new ArrayList<Comment>(maxTagCoveringComments.keySet());
			//			Collections.shuffle(keys2shuffle);
			//			for (Comment cm : keys2shuffle)
			//				shuffledMaxTagCoveringComments
			//						.put(cm, maxTagCoveringComments.get(cm));
			//			
			//			//now chose first edu.wpi.sgf.comment in there
			//			for(Map.Entry<Comment, Integer> each : shuffledMaxTagCoveringComments.entrySet())
			//				eligibleComments.add(each.getKey());

		}

//		for(Comment cm : allComments)
//			if(cm.getStrengthLowerBount() < move.getMoveStrength()
//					&&cm.getStrengthUpperBound() > move.getMoveStrength())
//				eligibleComments.add(cm);
		
		for(Comment cm : allComments)
			if(cm.getTags().contains("competition"))
				eligibleComments.add(cm);
		
		//add defauly package?
		
		Collections.shuffle(eligibleComments);

		if(eligibleComments.isEmpty())
			return null;
		return eligibleComments.get(0);

	}

	public void make(Comment cm) {
		gtts.say(cm.getContent());
	}

	//testing main
	public static void main(String[] args) {
		CommentingManager ch = new CommentingManager();
		List<String> a = new ArrayList<String>();
		List<String> b = new ArrayList<String>();
		a.add("hey"); a.add("hoy");
		b.add("bay"); b.add("boy");

		Comment cm1 = new Comment("Hello", a, 0, 1, 1);
		Comment cm2 = new Comment("Heeho", b, 0, 1, 1);
		Comment cm3 = new Comment("Hesho", b, 0, 1, 1);
		Comment cm4 = new Comment("Haeho", b, 0, 1, 1);

		ch.eachCommentsTagCovering.put(cm1, 5);
		ch.eachCommentsTagCovering.put(cm3, 6);
		ch.eachCommentsTagCovering.put(cm2, 3);
		ch.eachCommentsTagCovering.put(cm4, 1);

		System.out.println(ch.eachCommentsTagCovering);

		Ordering<Comment> valueComparator = Ordering.natural()
				.onResultOf(Functions.forMap(ch.eachCommentsTagCovering)).reverse();
		ch.sortedEachCommentsTagCovering = 
				ImmutableSortedMap.copyOf(ch.eachCommentsTagCovering, valueComparator);

		System.out.println("SORTED:" + ch.sortedEachCommentsTagCovering);

		ScenarioManager sm = new ScenarioManager();

		System.out.println(sm.getCurrentScenario());

		//		ch.make(new Comment("hello"));
	}


}
