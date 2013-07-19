package edu.wpi.sgf.comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.GameLogicState;

//this class is a temp one for revising 
public class CommentingManager2 {

	private Tts tts;
	@SuppressWarnings("unused")
   private Gtts gtts;
	private CommentLibraryHandler libHandler;
	private List<Comment> eligibleComments;
	private List<Comment> allComments;
	TreeMap<List<Integer>, Comment> commentsTagCoverings;
	
	public CommentingManager2(){
		
		libHandler = new CommentLibraryHandler();
		allComments = new ArrayList<Comment>();
		allComments.addAll(libHandler.retrieveAllCommentsFromLibrary());
		commentsTagCoverings = new TreeMap<List<Integer>, Comment>(); 
		
		try {
			tts.init("kevin16");
		} catch (Exception e) {
			System.out.println("FreeTTS exception.");
			e.printStackTrace();
		}
		
	}
	
	public Comment pickCommentForHumanMove(GameLogicState gameState,
			AnnotatedLegalMove humanMove){
				
		eligibleComments.clear();
		
		//gotta get annotations, translate to comment tags, pick comment
		
		if(gameState.agentWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("agentWon"))
					eligibleComments.add(cm);
		if(gameState.userWins)
			for(Comment cm : allComments)
				if(cm.getTags().contains("userWins"))
					eligibleComments.add(cm);
		
		if(!eligibleComments.isEmpty()){
			Collections.shuffle(eligibleComments);
			eligibleComments.get(new Random().nextInt(
					eligibleComments.size() - 1));
			return eligibleComments.get(
					eligibleComments.size() - 1);
		}
		
		return null;
		
	}
	
	public Comment pickCommentForAgentMove(GameLogicState gameState, 
			AnnotatedLegalMove agentMove){
				
		eligibleComments.clear();
		
		return null;
		
	}
	
	
}
