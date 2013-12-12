package edu.wpi.always.ttt;

import java.util.List;
import java.util.Random;
import edu.wpi.disco.rt.menu.*;

public class WhoPlaysFirst extends TTTAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static List<String> humanCommentOptions;
   private static String currentAgentComment = "";
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";

   public WhoPlaysFirst(final TTTStateContext context) {
      super("Do you want to play the first move or should I?", context);
      choice("Let me play first", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new Limbo(context);
         }
      });
      choice("You go ahead", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new AgentPlayDelay(context);
         }
      });
   }
   @Override
   public void enter() {
      if(!TTTClient.gameOver)
         getContext().getTTTUI().startPluginForTheFirstTime(this);
      else {
         TTTClient.gameOver = false;
         getContext().getTTTUI().resetGame();
         getContext().getTTTUI().updatePlugin(this);
      }
      getContext().getTTTUI().makeBoardPlayable();
   }
   @Override
   public void afterLimbo() {
      skipTo(new CreateCommentsAfterLimbo(getContext()));
   }

   //Limbo as waiting for user move
   public static class Limbo extends TTTAdjacencyPairImpl { 
      public Limbo(final TTTStateContext context){
         super("", context);
         //super(currentAgentComment, context);
      }
      @Override
      public void enter() {
         if(TTTClient.gameOver){
            humanCommentOptions = getContext().getTTTUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getTTTUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         getContext().getTTTUI().makeBoardPlayable();
         getContext().getTTTUI().updatePlugin(this);
         TTTClient.gazeDirection = "board";
      }
      @Override
      public void afterLimbo() {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
   }

   public static class CreateCommentsAfterLimbo extends TTTAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final TTTStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().prepareAgentCommentForAMoveBy(
               HUMAN_IDENTIFIER);
         currentAgentComment = getContext().getTTTUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getTTTUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), HUMAN_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), HUMAN_IDENTIFIER));
      }
   }

   public static class AgentPlayDelay extends TTTAdjacencyPairImpl {
      public AgentPlayDelay(final TTTStateContext context){
         super(WhatAgentSaysIfHumanDoesNotChooseAComment, context);
         WhatAgentSaysIfHumanDoesNotChooseAComment = "";
      }
      @Override
      public void enter(){
         if(TTTClient.gameOver){
            humanCommentOptions = getContext().getTTTUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getTTTUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         TTTClient.gazeDirection = "thinking";
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().triggerAgentPlayTimer();
         getContext().getTTTUI().prepareAgentMove();
      }
      @Override
      public void afterAgentPlayDelay() {
         skipTo(new AgentPlays(getContext()));
      }
      @Override
      protected void afterAgentPlayingGazeDelay () {
         TTTClient.gazeDirection = "board";
      }
   }

   public static class AgentPlays extends TTTAdjacencyPairImpl {
      public AgentPlays(final TTTStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         //TTTClient.gazeDirection = "board";
         getContext().getTTTUI().playAgentMove(this);
         getContext().getTTTUI().prepareAgentCommentForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getTTTUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getTTTUI()
               .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
      }
   }

   public static class AgentComments extends TTTAdjacencyPairImpl {
      int playerIdentifier;
      public AgentComments(final TTTStateContext context
            , final int playerIdentifier){
         //super(currentAgentComment, context);
         super("", context);
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         getContext().getTTTUI().prepareAgentCommentForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getTTTUI()
               .getCurrentAgentComment();
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().triggerNextStateTimer(this);
         TTTClient.gazeDirection = "sayandgaze";
      }
      @Override
      public void goToNextState () {
         if(playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else
            skipTo(new AgentPlayDelay(getContext()));
      }
   }

   public static class HumanComments extends TTTAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final TTTStateContext context
            , final int playerIdentifier){
         super("", context);
         TTTClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!TTTClient.gameOver){
            for(String eachCommentOption : humanCommentOptions)
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getTTTUI().cancelHumanCommentingTimer();
                     if (playerIdentifier == AGENT_IDENTIFIER)
                        return new Limbo(getContext());
                     return new AgentPlayDelay(getContext());
                  }
               });
            if(playerIdentifier == HUMAN_IDENTIFIER){
               choice("Your turn", new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getTTTUI().cancelHumanCommentingTimer();
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
      public void afterLimbo() {
         TTTClient.gazeDirection = "board";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void enter() {
         TTTClient.gazeDirection = "useronce";
         if(TTTClient.gameOver){
            skipTo(new gameOverDialogue(getContext()));
            TTTClient.gazeDirection = "board";
         }
         currentAgentComment = "";
         //TTTClient.gazeDirection = "user";
         humanCommentOptions = getContext().getTTTUI()
               .getCurrentHumanCommentOptionsForAMoveBy(playerIdentifier);
         getContext().getTTTUI().updatePlugin(this);
         //getContext().getTTTUI().triggerHumanCommentingTimer();
         if(playerIdentifier == AGENT_IDENTIFIER)
            getContext().getTTTUI().makeBoardPlayable();
      }
   }

   public static class gameOverDialogue extends TTTAdjacencyPairImpl {
      public gameOverDialogue(final TTTStateContext context){
         super("", context);
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override

               public AdjacencyPair run () {
                  //getContext().getTTTUI().cancelHumanCommentingTimer();
                  return new gameOver(getContext());
               }
            });
         choice("Anyway", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               //getContext().getTTTUI().cancelHumanCommentingTimer();
               return new gameOver(getContext());
            }
         });
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         TTTClient.gazeDirection = "sayandgazegameover";
         humanCommentOptions = getContext().getTTTUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
         //getContext().getTTTUI().triggerHumanCommentingTimer();
      }
      @Override
      public void afterTimeOut() {
         skipTo(new gameOver(getContext()));
      }
   }

   public static class gameOver extends TTTAdjacencyPairImpl {
      public gameOver(final TTTStateContext context){
         super("Now do you want to play again?", context);
         choice("Sure", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new WhoPlaysFirst(context);
            }
         });
         choice("Not really", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new endingPlugin(context);
            }
         });
      }
      @Override 
      public void enter(){
         TTTClient.gazeDirection = "";
         //TTTClient.gazeDirection = "user";
         TTTClient.gameOver = true;
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
      }
   }
   
   public static class endingPlugin extends TTTAdjacencyPairImpl {
      public endingPlugin(final TTTStateContext context){
         super("", context);
      }
      @Override 
      public void enter(){
         getContext().getSchema().stop();
      }
   }

   public static String getCurrentAgentComment () {
      return currentAgentComment;
   }

}
