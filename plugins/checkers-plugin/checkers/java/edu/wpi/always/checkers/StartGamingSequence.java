package edu.wpi.always.checkers;

import java.util.List;
import java.util.Random;
import edu.wpi.disco.rt.menu.*;

public class StartGamingSequence extends CheckersAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static List<String> humanCommentOptions;
   private static String currentAgentComment = "";
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";

   public StartGamingSequence(final CheckersStateContext context) {
      super("Let's play checkers", context);
      choice("Ok", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new AgentPlayDelay(getContext());
         }
      });
   }
   @Override
   public void enter() {
      if(!CheckersClient.gameOver)
         getContext().getCheckersUI().startPluginForTheFirstTime(this);
      else {
         CheckersClient.gameOver = false;
         getContext().getCheckersUI().resetGame();
         getContext().getCheckersUI().updatePlugin(this);
      }
//      getContext().getCheckersUI().makeBoardPlayable();
   }

   //Limbo as waiting for user move
   public static class Limbo extends CheckersAdjacencyPairImpl { 
      public Limbo(final CheckersStateContext context){
         super("", context);
         //super(currentAgentComment, context);
      }
      @Override
      public void enter() {
         if(CheckersClient.gameOver){
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
//         getContext().getCheckersUI().makeBoardPlayable();
         getContext().getCheckersUI().updatePlugin(this);
         CheckersClient.gazeDirection = "board";
      }
      @Override
      public void humanMoveReceived() {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
   }

   public static class CreateCommentsAfterLimbo extends CheckersAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final CheckersStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
//         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().prepareAgentCommentForAMoveBy(
               HUMAN_IDENTIFIER);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), HUMAN_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), HUMAN_IDENTIFIER));
      }
   }

   public static class AgentPlayDelay extends CheckersAdjacencyPairImpl {
      public AgentPlayDelay(final CheckersStateContext context){
         super(WhatAgentSaysIfHumanDoesNotChooseAComment, context);
         WhatAgentSaysIfHumanDoesNotChooseAComment = "";
      }
      @Override
      public void enter(){
         if(CheckersClient.gameOver){
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         CheckersClient.gazeDirection = "thinking";
//         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().updatePlugin(this);
         getContext().getCheckersUI().triggerAgentPlayTimer();
         getContext().getCheckersUI().prepareAgentMove();
      }
      @Override
      protected void afterAgentPlayDelay() {
         skipTo(new AgentPlays(getContext()));
      }
      @Override
      protected void afterAgentPlayingGazeDelay () {
         CheckersClient.gazeDirection = "board";
      }
   }

   public static class AgentPlays extends CheckersAdjacencyPairImpl {
      public AgentPlays(final CheckersStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         //CheckersClient.gazeDirection = "board";
         getContext().getCheckersUI().playAgentMove(this);
         getContext().getCheckersUI().prepareAgentCommentForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
      }
   }

   public static class AgentComments extends CheckersAdjacencyPairImpl {
      int playerIdentifier;
      public AgentComments(final CheckersStateContext context
            , final int playerIdentifier){
         //super(currentAgentComment, context);
         super("", context);
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         getContext().getCheckersUI().prepareAgentCommentForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         getContext().getCheckersUI().updatePlugin(this);
         getContext().getCheckersUI().triggerNextStateTimer();
         CheckersClient.gazeDirection = "sayandgaze";
      }
      @Override
      public void goToNextState () {
         if(playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else
            skipTo(new AgentPlayDelay(getContext()));
      }
   }

   public static class HumanComments extends CheckersAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final CheckersStateContext context
            , final int playerIdentifier){
         super("", context);
         CheckersClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!CheckersClient.gameOver){
            for(String eachCommentOption : humanCommentOptions)
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
                     if (playerIdentifier == AGENT_IDENTIFIER)
                        return new Limbo(getContext());
                     return new AgentPlayDelay(getContext());
                  }
               });
            if(playerIdentifier == HUMAN_IDENTIFIER){
               choice("Your turn", new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
                     if (playerIdentifier == AGENT_IDENTIFIER)
                        return new Limbo(getContext());
                     return new AgentPlayDelay(getContext());
                  }
               });
            }
         }
      }
      @Override
      public void afterTimeOut() {
         if(playerIdentifier == HUMAN_IDENTIFIER){
            WhatAgentSaysIfHumanDoesNotChooseAComment = "OK";
            skipTo(new AgentPlayDelay(getContext()));
         }
      }
      @Override 
      public void humanMoveReceived() {
         CheckersClient.gazeDirection = "board";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void enter() {
         CheckersClient.gazeDirection = "useronce";
         if(CheckersClient.gameOver){
            skipTo(new gameOverDialogue(getContext()));
            CheckersClient.gazeDirection = "board";
         }
         currentAgentComment = "";
         //CheckersClient.gazeDirection = "user";
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsForAMoveBy(playerIdentifier);
         getContext().getCheckersUI().updatePlugin(this);
         //getContext().getCheckersUI().triggerHumanCommentingTimer();
//         if(playerIdentifier == AGENT_IDENTIFIER)
//            getContext().getCheckersUI().makeBoardPlayable();
      }
   }

   public static class gameOverDialogue extends CheckersAdjacencyPairImpl {
      public gameOverDialogue(final CheckersStateContext context){
         super("", context);
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override

               public AdjacencyPair run () {
                  //getContext().getCheckersUI().cancelHumanCommentingTimer();
                  return new gameOver(getContext());
               }
            });
         choice("Anyway", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               //getContext().getCheckersUI().cancelHumanCommentingTimer();
               return new gameOver(getContext());
            }
         });
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         CheckersClient.gazeDirection = "sayandgazegameover";
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
//         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().updatePlugin(this);
         //getContext().getCheckersUI().triggerHumanCommentingTimer();
      }
      @Override
      public void afterTimeOut() {
         skipTo(new gameOver(getContext()));
      }
   }

   public static class gameOver extends CheckersAdjacencyPairImpl {
      public gameOver(final CheckersStateContext context){
         super("Now do you want to play again?", context);
         choice("Sure", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new StartGamingSequence(context);
            }
         });
         choice("Not really", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new gameOver(context);
            }
         });
      }
      @Override 
      public void enter(){
         CheckersClient.gazeDirection = "";
         //CheckersClient.gazeDirection = "user";
         CheckersClient.gameOver = true;
//         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().updatePlugin(this);
      }
   }

   public static String getCurrentAgentComment () {
      return currentAgentComment;
   }

}
