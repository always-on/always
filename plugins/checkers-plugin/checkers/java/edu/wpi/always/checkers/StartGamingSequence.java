package edu.wpi.always.checkers;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import edu.wpi.disco.rt.menu.*;

public class StartGamingSequence extends CheckersAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static List<String> humanCommentOptions;
   private static String currentAgentComment = "";
   private static String currentAgentResponse = "";
   private static List<String> humanResponseOptions = 
         new ArrayList<String>();
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";
   private static String clarificationString = "";
   private static String whichClarification = "";
   
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
//      getContext().getCheckersUI().makeBoardPlayable(); //NOT YET. Only if user played first or given the option to play first
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
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         getContext().getCheckersUI().makeBoardPlayable();
         getContext().getCheckersUI().updatePlugin(this);
         CheckersClient.gazeDirection = "board";
      }
      @Override
      public void humanMoveReceived () {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void shouldHaveJumped () {
         //perhaps over-designed a little, 
         //but makes agent understanding image more compelling.
         whichClarification = "jump";
         if(!CheckersClient.userJumpedAtLeastOnceInThisTurn)
            clarificationString = 
            CheckersClient.shouldHaveJumpedClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .shouldHaveJumpedClarificationStringOptions.size()));
         else
            clarificationString = 
            CheckersClient.shouldJumpAgainClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .shouldJumpAgainClarificationStringOptions.size()));

         CheckersClient.gazeDirection = "";
         skipTo (new Clarification(getContext()));
      }
      @Override
      public void humanTouchedAgentStuff (int howManyTimes) {
         CheckersClient.gazeDirection = "board";
         whichClarification = "touch";
         if(howManyTimes < 3)
            clarificationString = 
            CheckersClient.humantouchedAgentCheckerClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .humantouchedAgentCheckerClarificationStringOptions.size()));
         else{
            clarificationString = 
                  CheckersClient.humantouchedTooMuchClarificationStringOptions
                  .get(new Random().nextInt(CheckersClient
                        .humantouchedTooMuchClarificationStringOptions.size()));
            whichClarification += "toomuch";
         }
         skipTo (new Clarification(getContext()));
      }
   }
   //the class below is used for a handful of situations, reused by cases.
   public static class Clarification extends CheckersAdjacencyPairImpl { 
      public Clarification(final CheckersStateContext context){
         super(clarificationString, context);
         if(whichClarification.equals("jump")){
            choice("Got it!", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new Limbo(getContext());
               }
            });
         }
         else if(whichClarification.equals("touch")){
            choice("Oh, ok", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new Limbo(getContext());
               }
            });
         }
         else if(whichClarification.equals("touchtoomuch")){
            choice("I'm now ready to let go", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new Limbo(getContext());
               }
            });
         }
//         else if(whichClarification.equals("myturn")){
//            choice("Oh, go ahead", new DialogStateTransition() {
//               @Override
//               public AdjacencyPair run () {
//                  return new AgentPlayDelay(getContext());
//               }
//            });
//         }
      }
      @Override
      public void enter() {
         getContext().getCheckersUI().updatePlugin(this);
         CheckersClient.gazeDirection = "";
      }
      @Override
      public void humanMoveReceived() {
         currentAgentComment = "";
         CheckersClient.gazeDirection = "board";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void shouldHaveJumped () {
         //here again, to be robust
         whichClarification = "jump";
         if(!CheckersClient.userJumpedAtLeastOnceInThisTurn)
            clarificationString = 
            CheckersClient.shouldHaveJumpedClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .shouldHaveJumpedClarificationStringOptions.size()));
         else
            clarificationString = 
            CheckersClient.shouldJumpAgainClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .shouldJumpAgainClarificationStringOptions.size()));
         skipTo (new Clarification(getContext()));
      }
      @Override
      public void humanTouchedAgentStuff (int howManyTimes) {
       //here again, to be robust
         CheckersClient.gazeDirection = "board";
         whichClarification = "touch";
         if(howManyTimes < 3)
            clarificationString = 
            CheckersClient.humantouchedAgentCheckerClarificationStringOptions
            .get(new Random().nextInt(CheckersClient
                  .humantouchedAgentCheckerClarificationStringOptions.size()));
         else{
            clarificationString = 
                  CheckersClient.humantouchedTooMuchClarificationStringOptions
                  .get(new Random().nextInt(CheckersClient
                        .humantouchedTooMuchClarificationStringOptions.size()));
            whichClarification += "toomuch";
         }
         skipTo (new Clarification(getContext()));
      }
   }

   public static class CreateCommentsAfterLimbo extends CheckersAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final CheckersStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               HUMAN_IDENTIFIER);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(HUMAN_IDENTIFIER);
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
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            CheckersClient.
            userJumpedAtLeastOnceInThisTurn = false;
            skipTo(new gameOverDialogue(getContext()));
         }
         CheckersClient.gazeDirection = "thinking";
         CheckersClient.userJumpedAtLeastOnceInThisTurn = false;
         getContext().getCheckersUI().makeBoardUnplayable();
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
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
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
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         getContext().getCheckersUI().updatePlugin(this);
         getContext().getCheckersUI().triggerNextStateTimer();
         CheckersClient.gazeDirection = "sayandgaze";
      }
      @Override
      public void goToNextState () {
         skipTo(new HumanResponse(
               getContext(), playerIdentifier));
      }
   }
   
   public static class HumanResponse extends CheckersAdjacencyPairImpl {
      int playerIdentifier;
      public HumanResponse(final CheckersStateContext context
            , final int playerIdentifier){
         super("", context);
         CheckersClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!CheckersClient.gameOver){
            for(String eachCommentOption : humanResponseOptions)
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
         //allows skipping the comment by just playing
         //only if it is user's turn, i.e. agent just played
         if (playerIdentifier == AGENT_IDENTIFIER){
            CheckersClient.gazeDirection = "board";
            skipTo(new CreateCommentsAfterLimbo(getContext()));
         }
      }
      @Override
      public void shouldHaveJumped () {
         //allows skipping the comment
         //(refer to comments of same overridden method in Limbo)
         if (playerIdentifier == AGENT_IDENTIFIER){
            whichClarification = "jump";
            if(!CheckersClient.userJumpedAtLeastOnceInThisTurn)
               clarificationString = 
               CheckersClient.shouldHaveJumpedClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .shouldHaveJumpedClarificationStringOptions.size()));
            else
               clarificationString = 
               CheckersClient.shouldJumpAgainClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .shouldJumpAgainClarificationStringOptions.size()));

            CheckersClient.gazeDirection = "";
            skipTo (new Clarification(getContext()));
         }
         else {
            //to clarify it is agent's turn?
//            whichClarification = "myturn";
//            CheckersClient.gazeDirection = "";
//            clarificationString = "Wait, I think it's my turn";
//            skipTo (new Clarification(getContext()));
         }
      }
      @Override
      public void humanTouchedAgentStuff (int howManyTimes) {
         if (playerIdentifier == AGENT_IDENTIFIER){
            CheckersClient.gazeDirection = "board";
            whichClarification = "touch";
            if(howManyTimes < 3)
               clarificationString = 
               CheckersClient.humantouchedAgentCheckerClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .humantouchedAgentCheckerClarificationStringOptions.size()));
            else{
               clarificationString = 
                     CheckersClient.humantouchedTooMuchClarificationStringOptions
                     .get(new Random().nextInt(CheckersClient
                           .humantouchedTooMuchClarificationStringOptions.size()));
               whichClarification += "toomuch";
            }
            skipTo (new Clarification(getContext()));
         }
         else{
            //to clarify it is agent's turn?
//            whichClarification = "myturn";
//            clarificationString = "Wait, I think it's my turn";
//            CheckersClient.gazeDirection = "";
//            skipTo (new Clarification(getContext()));
         }
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
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(playerIdentifier);
         getContext().getCheckersUI().updatePlugin(this);
         //getContext().getCheckersUI().triggerHumanCommentingTimer();
//         if(playerIdentifier == AGENT_IDENTIFIER)
            getContext().getCheckersUI().makeBoardPlayable();
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
            for(final String eachCommentOption : humanCommentOptions)
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
                     return new AgentResponse(
                           getContext(), playerIdentifier, eachCommentOption);
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
         //allows skipping the comment by just playing
         //only if it is user's turn, i.e. agent just played
         if (playerIdentifier == AGENT_IDENTIFIER){
            CheckersClient.gazeDirection = "board";
            skipTo(new CreateCommentsAfterLimbo(getContext()));
         }
      }
      @Override
      public void shouldHaveJumped () {
         //allows skipping the comment
         //(refer to comments of same overridden method in Limbo)
         if (playerIdentifier == AGENT_IDENTIFIER){
            whichClarification = "jump";
            if(!CheckersClient.userJumpedAtLeastOnceInThisTurn)
               clarificationString = 
               CheckersClient.shouldHaveJumpedClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .shouldHaveJumpedClarificationStringOptions.size()));
            else
               clarificationString = 
               CheckersClient.shouldJumpAgainClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .shouldJumpAgainClarificationStringOptions.size()));

            CheckersClient.gazeDirection = "";
            skipTo (new Clarification(getContext()));
         }
         else {
            //to clarify it is agent's turn?
//            whichClarification = "myturn";
//            CheckersClient.gazeDirection = "";
//            clarificationString = "Wait, I think it's my turn";
//            skipTo (new Clarification(getContext()));
         }
      }
      @Override
      public void humanTouchedAgentStuff (int howManyTimes) {
         if (playerIdentifier == AGENT_IDENTIFIER){
            CheckersClient.gazeDirection = "board";
            whichClarification = "touch";
            if(howManyTimes < 3)
               clarificationString = 
               CheckersClient.humantouchedAgentCheckerClarificationStringOptions
               .get(new Random().nextInt(CheckersClient
                     .humantouchedAgentCheckerClarificationStringOptions.size()));
            else{
               clarificationString = 
                     CheckersClient.humantouchedTooMuchClarificationStringOptions
                     .get(new Random().nextInt(CheckersClient
                           .humantouchedTooMuchClarificationStringOptions.size()));
               whichClarification += "toomuch";
            }
            skipTo (new Clarification(getContext()));
         }
         else{
            //to clarify it is agent's turn?
//            whichClarification = "myturn";
//            clarificationString = "Wait, I think it's my turn";
//            CheckersClient.gazeDirection = "";
//            skipTo (new Clarification(getContext()));
         }
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
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(playerIdentifier);
         getContext().getCheckersUI().updatePlugin(this);
         //getContext().getCheckersUI().triggerHumanCommentingTimer();
//         if(playerIdentifier == AGENT_IDENTIFIER)
            getContext().getCheckersUI().makeBoardPlayable();
      }
   }
   
   public static class AgentResponse extends CheckersAdjacencyPairImpl {
      int playerIdentifier;
      String humanChoosenComment;
      public AgentResponse(final CheckersStateContext context
            , final int playerIdentifier, String humanChoosenComment){
         //super(currentAgentComment, context);
         super("", context);
         this.playerIdentifier = playerIdentifier;
         this.humanChoosenComment = humanChoosenComment;
      }
      @Override 
      public void enter(){
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               playerIdentifier);
         currentAgentResponse = getContext().getCheckersUI()
               .getCurrentAgentResponse(humanChoosenComment);
         getContext().getCheckersUI().updatePlugin(this);
         getContext().getCheckersUI().triggerNextStateTimer();
         CheckersClient.gazeDirection = "sayandgazeresp";
      }
      @Override
      public void goToNextState () {
         if (playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else 
            skipTo(new AgentPlayDelay(getContext()));
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
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(HUMAN_IDENTIFIER);
         getContext().getCheckersUI().makeBoardUnplayable();
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
         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().updatePlugin(this);
      }
   }

   public static String getCurrentAgentComment () {
      return currentAgentComment;
   }
   public static String getCurrentAgentResponse () {
      return currentAgentResponse;
   }

}
