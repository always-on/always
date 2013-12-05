package edu.wpi.sgf.comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.GameLogicState;

public class CommentingManager {

   protected CommentLibraryHandler libHandler;

   public CommentingManager(){

   }

   public List<String> getHumanCommentingOptionsForHumanMove(
         GameLogicState gameState, AnnotatedLegalMove humanMove, 
         List<String> gameSpecificTags) {

      //if win or tie situation
      if(gameState.agentWins)
         return shuffleAndGetMax3(
               libHandler.getContentsOfTheseComments(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getHumanComments())));
      else if(gameState.userWins)
         return shuffleAndGetMax3( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getHumanComments())));
      else if(gameState.tie)
         return shuffleAndGetMax3( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getTieCommentsAmong(
                           libHandler.getHumanComments())));
      //if still playing
      else
         return shuffleAndGetMax3(  
               libHandler.getContentsOfTheseComments(
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getHumanComments()), "human"),
                                             //humanMove.getMoveStrength()), 
                                             null, gameSpecificTags, null)));
   }

   public List<String> getHumanCommentingOptionsForAgentMove(
         GameLogicState gameState, AnnotatedLegalMove agentMove,
         List<String> gameSpecificTags) {

      //if win or tie situation
      if(gameState.agentWins)
         return shuffleAndGetMax3(
               libHandler.getContentsOfTheseComments(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getHumanComments())));
      else if(gameState.userWins)
         return shuffleAndGetMax3( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getHumanComments())));
      else if(gameState.tie)
         return shuffleAndGetMax3( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getTieCommentsAmong(
                           libHandler.getHumanComments())));
      //if still playing
      else
         return shuffleAndGetMax3(  
               libHandler.getContentsOfTheseComments(
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getHumanComments()), "agent"),
                                             //agentMove.getMoveStrength()), 
                                             null, gameSpecificTags, null)));
   }

   public String getAgentCommentForAgentMove(
         GameLogicState gameState, AnnotatedLegalMove agentMove,
         List<String> scenariosTags, List<String> gameSpecificTags) {

      //TODO ADD SCENARIO, or got from callers and already in? Which better? 
      //gameName is null right now, should it be gotten from somewhere?

      //if win or tie situation
      if(gameState.agentWins)
         return getOneRandomlyAmong(
               libHandler.getContentsOfTheseComments(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getAgentComments())));
      else if(gameState.userWins)
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getAgentComments())));
      else if(gameState.tie)
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getTieCommentsAmong(
                           libHandler.getAgentComments())));
      //if still playing
      else
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getAgentComments()), "agent"),
                                             //agentMove.getMoveStrength()), 
                                             scenariosTags, gameSpecificTags, null)));
   }

   public String getAgentCommentForHumanMove(
         GameLogicState gameState, AnnotatedLegalMove humanMove,
         List<String> scenariosTags, List<String> gameSpecificTags) {

      //TODO ADD SCENARIO, or got from callers and already in ? Which better?
      //gameName is null right now, should it be gotten from somewhere?

      //if win or tie situation
      if(gameState.agentWins)
         return getOneRandomlyAmong(
               libHandler.getContentsOfTheseComments(
                     libHandler.getAgentWinningCommentsAmong(
                           libHandler.getAgentComments())));
      else if(gameState.userWins)
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getHumanWinningCommentsAmong(
                           libHandler.getAgentComments())));
      else if(gameState.tie)
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.getTieCommentsAmong(
                           libHandler.getAgentComments())));
      //if still playing
      else
         return getOneRandomlyAmong( 
               libHandler.getContentsOfTheseComments(
                     libHandler.prioritizeByTags(
                           //libHandler.getValidCommentsByStrength(
                                 libHandler.getCmForMakingOnPlayerAmong(
                                       libHandler.getStillPlayingCommentsAmong(
                                             libHandler.getAgentComments()), "human"),
                                             //humanMove.getMoveStrength()), 
                                             scenariosTags, gameSpecificTags, null)));
   }


   //utilities:
   private List<String> shuffleAndGetMax3(List<String> someStrings){
      List<String> final3 = new ArrayList<String>();
      if(someStrings.size() > 3){
         Collections.shuffle(someStrings);
         for(int i = 0; i < 3; i ++)
            final3.add(someStrings.get(i));
      }
      else
         final3.addAll(someStrings);
      Collections.shuffle(final3);
      return final3;
   }
   private String getOneRandomlyAmong(List<String> someStrings){
      Collections.shuffle(someStrings);
      return someStrings.get(new Random().
            nextInt(someStrings.size()));
   }

}
