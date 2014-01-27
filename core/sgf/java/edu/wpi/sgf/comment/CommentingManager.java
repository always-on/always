package edu.wpi.sgf.comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.GameLogicState;

public class CommentingManager {

   protected CommentLibraryHandler libHandler;

   private static Random random;
   private static final int RANDOM_SEED = 12345;
   
   public CommentingManager(){
      
      random = new Random(RANDOM_SEED);

   }

   public List<Comment> getHumanCommentingOptionsAndAnAgentResponseForHumanMove(
         GameLogicState gameState, AnnotatedLegalMove humanMove, 
         List<String> gameSpecificTags) {

      //if win or tie situation
      if(gameState.agentWins)
         return shuffleAndGetMax3(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getHumanComments()));
      else if(gameState.userWins)
         return shuffleAndGetMax3( 
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getHumanComments()));
      else if(gameState.tie)
         return shuffleAndGetMax3( 
                     libHandler.getTieCommentsAmong(
                           libHandler.getHumanComments()));
      //if still playing
      else
         return shuffleAndGetMax3(  
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getHumanComments()), "human"),
                                             //humanMove.getMoveStrength()), 
                                             null, gameSpecificTags, null));
   }

   public List<Comment> getHumanCommentingOptionsForAgentMove(
         GameLogicState gameState, AnnotatedLegalMove agentMove,
         List<String> gameSpecificTags) {

      //if win or tie situation
      if(gameState.agentWins)
         return shuffleAndGetMax3(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getHumanComments()));
      else if(gameState.userWins)
         return shuffleAndGetMax3( 
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getHumanComments()));
      else if(gameState.tie)
         return shuffleAndGetMax3( 
                     libHandler.getTieCommentsAmong(
                           libHandler.getHumanComments()));
      //if still playing
      else
         return shuffleAndGetMax3(  
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                           libHandler.getCmForMakingOnPlayerAmong(
                                 libHandler.getStillPlayingCommentsAmong(
                                       libHandler.getHumanComments()), "agent"),
                                       //agentMove.getMoveStrength()), 
                                       null, gameSpecificTags, null));
      
   }

   public Comment getAgentCommentForAgentMove(
         GameLogicState gameState, AnnotatedLegalMove agentMove,
         List<String> scenariosTags, List<String> gameSpecificTags) {

      //TODO ADD SCENARIO, or got from callers and already in? Which better? 
      //gameName is null right now, should it be gotten from somewhere?

      //if win or tie situation
      if(gameState.agentWins)
         return getOneRandomlyAmong(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getAgentComments()));
      else if(gameState.userWins)
         return getOneRandomlyAmong( 
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getAgentComments()));
      else if(gameState.tie)
         return getOneRandomlyAmong( 
                     libHandler.getTieCommentsAmong(
                           libHandler.getAgentComments()));
      //if still playing
      else
         return getOneRandomlyAmong( 
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getAgentComments()), "agent"),
                                             //agentMove.getMoveStrength()), 
                                             scenariosTags, gameSpecificTags, null));
   }

   public Comment getAgentCommentForHumanMove(
         GameLogicState gameState, AnnotatedLegalMove humanMove,
         List<String> scenariosTags, List<String> gameSpecificTags) {

      //TODO ADD SCENARIO, or got from callers and already in ? Which better?
      //gameName is null right now, should it be gotten from somewhere?

      //if win or tie situation
      if(gameState.agentWins)
         return getOneRandomlyAmong(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getAgentComments()));
      else if(gameState.userWins)
         return getOneRandomlyAmong( 
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getAgentComments()));
      else if(gameState.tie)
         return getOneRandomlyAmong( 
                     libHandler.getTieCommentsAmong(
                           libHandler.getAgentComments()));
      //if still playing
      else
         return getOneRandomlyAmong( 
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getAgentComments()), "human"),
                                             //humanMove.getMoveStrength()), 
                                             scenariosTags, gameSpecificTags, null));
   }


   //utilities:
   public static <E> List<E> shuffleAndGetMax3(
         List<E> someObjects){
      if(someObjects.isEmpty())
         return null;
      List<E> final3 = new ArrayList<E>();
      if(someObjects.size() > 3){
         Collections.shuffle(someObjects, random);
         for(int i = 0; i < 3; i ++)
            final3.add(someObjects.get(
                  random.nextInt(someObjects.size())));
      }
      else
         final3.addAll(someObjects);
      Collections.shuffle(someObjects, random);
      return final3;
   }
   
   static <E> E getOneRandomlyAmong(List<E> someObjects){
      if(someObjects.isEmpty())
         return null;
      Collections.shuffle(someObjects, random);
      return someObjects.get(random.
            nextInt(someObjects.size()));
   }

}
