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
      super("Let's play checkers, I will play gray and you play red.", context);
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
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getCheckersUI().makeBoardPlayable();
            getContext().getCheckersUI().updatePlugin(this);
            CheckersClient.gazeDirection = "board";
         }
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
            choice("Ok!", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new Limbo(getContext());
               }
            });
         }
         else if(whichClarification.equals("touchtoomuch")){
            choice("Haha! fine!", new DialogStateTransition() {
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
         CheckersClient.userJumpedAtLeastOnceInThisTurn = false;
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
       // here again, to be robust
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
         Random rnd = new Random();
         if(rnd.nextBoolean() || rnd.nextBoolean() || CheckersClient.thereAreGameSpecificTags){
            //by 75% chance (or if there is game specific comment) here: full comment exchange
            CheckersClient.thereAreGameSpecificTags = false;
            if(new Random().nextBoolean())
               skipTo(new AgentComments(getContext(), HUMAN_IDENTIFIER));
            else
               skipTo(new HumanComments(getContext(), HUMAN_IDENTIFIER));
         }
         else{
            //by 25% chance here: no comment exchange
            skipTo(new AgentPlayDelay(getContext()));
         }
      }
   }

   public static class AgentPlayDelay extends CheckersAdjacencyPairImpl {
      public AgentPlayDelay(final CheckersStateContext context){
         super(WhatAgentSaysIfHumanDoesNotChooseAComment, context);
         WhatAgentSaysIfHumanDoesNotChooseAComment = "";
      }
      @Override
      public void enter(){
         CheckersClient.userJumpedAtLeastOnceInThisTurn = false;
         if(CheckersClient.gameOver){
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            CheckersClient.gazeDirection = "thinking";
            getContext().getCheckersUI().makeBoardUnplayable();
            getContext().getCheckersUI().updatePlugin(this);
            getContext().getCheckersUI().triggerAgentPlayTimer();
            getContext().getCheckersUI().prepareAgentMove();
         }
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
         CheckersClient.gazeDirection = "board";
         if(CheckersClient.gameOver){
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getCheckersUI().processAgentMove(this);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);

            if(!CheckersClient.moreJumpsPossible){
               Random rnd = new Random();
               if(rnd.nextBoolean() || rnd.nextBoolean() || CheckersClient.thereAreGameSpecificTags){
                  //by 75% chance (or if there is game specific comment) here: full comment exchange
                  CheckersClient.thereAreGameSpecificTags = false;
                  if(new Random().nextBoolean())
                     skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
                  else
                     skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
               }
               else{
                  //by 25% chance here: no comment exchange
                  skipTo(new Limbo(getContext()));
               }
            }
            else
               skipTo(new AgentPlaysMultiJump(getContext()));
         }
      }
   }
   
   public static class AgentPlaysMultiJump extends CheckersAdjacencyPairImpl {
      public AgentPlaysMultiJump(final CheckersStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         getContext().getCheckersUI().triggerAgentMultiJumpTimer(this);
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
      }
      @Override
      protected void agentMultiJumpConfirmed(){
         if(CheckersClient.moreJumpsPossible)
            skipTo(new AgentPlaysMultiJump(getContext()));
         else{
            Random rnd = new Random();
            if(rnd.nextBoolean() || rnd.nextBoolean() || CheckersClient.thereAreGameSpecificTags){
               //by 75% chance (or if there is game specific comment) here: full comment exchange
               CheckersClient.thereAreGameSpecificTags = false;
               if(new Random().nextBoolean())
                  skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
               else
                  skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
            }
            else{
               //by 25% chance here: no comment exchange
               skipTo(new Limbo(getContext()));
            }
         }
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
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
         getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getCheckersUI()
               .getCurrentAgentComment();
         humanResponseOptions.clear();
         try{
            humanResponseOptions.addAll(getContext().getCheckersUI()
                  .getCurrentHumanResponseOptions());
         }catch(Exception e){/*in case no response exists*/}
         if(CheckersClient.gameOver){
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getCheckersUI().updatePlugin(this);
            getContext().getCheckersUI().triggerNextStateTimer(this);
            CheckersClient.gazeDirection = "sayandgaze";
         }
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
            //if no response is there for this agent's comment
            //which human is responding to, then just go to 
            //whoever turn it is to play. (No your turn button)
            if(humanResponseOptions.isEmpty()){
               if(playerIdentifier == HUMAN_IDENTIFIER)
                  skipTo(new AgentPlayDelay(getContext()));
               else
                  skipTo(new Limbo(getContext()));
            }
            
            for(String eachCommentOption : humanResponseOptions){
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
                     if (playerIdentifier == AGENT_IDENTIFIER)
                        return new Limbo(getContext());
                     return new AgentPlayDelay(getContext());
                  }
               });
            }
            if(playerIdentifier == HUMAN_IDENTIFIER){
               choice("Your turn", new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
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
         currentAgentComment = "";
         //CheckersClient.gazeDirection = "user";
         humanCommentOptions = getContext().getCheckersUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(playerIdentifier);
         if(CheckersClient.gameOver){
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getCheckersUI().updatePlugin(this);
            //getContext().getCheckersUI().triggerHumanCommentingTimer();
            //         if(playerIdentifier == AGENT_IDENTIFIER)
            getContext().getCheckersUI().makeBoardPlayable();
         }
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
            for(final String eachCommentOption : humanCommentOptions){
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getCheckersUI().cancelHumanCommentingTimer();
                     return new AgentResponse(
                           getContext(), playerIdentifier, eachCommentOption);
                  }
               });
            }
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
            humanCommentOptions = getContext().getCheckersUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getCheckersUI().prepareAgentCommentUserResponseForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getCheckersUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
               humanResponseOptions.addAll(getContext().getCheckersUI()
                     .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(new Random().nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
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
         getContext().getCheckersUI().triggerNextStateTimer(this);
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

   public static class gameOverDialogueByAgent extends CheckersAdjacencyPairImpl {
      public gameOverDialogueByAgent(final CheckersStateContext context){
         super("", context);
      }
      @Override 
      public void enter(){
         getContext().getCheckersUI().triggerNextStateTimer(this);
         getContext().getCheckersUI().makeBoardUnplayable();
         CheckersClient.gazeDirection = "sayandgaze";
      }
      @Override
      public void goToNextState () {
         skipTo(new gameOverdrbh(getContext()));
      }
   }
   
   public static class gameOverDialogueByHuman extends CheckersAdjacencyPairImpl {
      public gameOverDialogueByHuman(final CheckersStateContext context){
         super("", context);
         if(humanCommentOptions.isEmpty())
            skipTo(new gameOverDialogueByAgent(getContext()));
         else{
            for(final String eachCommentOption : humanCommentOptions){
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     return new gameOverDialogueResponseByAgent(
                           getContext(), eachCommentOption);
                  }
               });
            }
            choice("Anyway", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new gameOver(getContext());
               }
            });
         }
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         getContext().getCheckersUI().makeBoardUnplayable();
      }
   }

   public static class gameOverdrbh extends CheckersAdjacencyPairImpl {
      public gameOverdrbh(final CheckersStateContext context){
         super("", context);
         if(humanResponseOptions.isEmpty())
            skipTo(new gameOverDialogueByAgent(getContext()));
         else{
            for(final String eachCommentOption : humanResponseOptions){
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     return new gameOver(getContext());
                  }
               });
            }
            choice("Anyway", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  return new gameOver(getContext());
               }
            });
         }
      }
      @Override 
      public void enter(){
         CheckersClient.gazeDirection = "";
         CheckersClient.gameOver = true;
      }
   }
   
   public static class gameOverDialogueResponseByAgent extends CheckersAdjacencyPairImpl {
      String humanChosenCm;
      public gameOverDialogueResponseByAgent(
            final CheckersStateContext context, String eachCommentOption){
         super("", context);
         this.humanChosenCm = eachCommentOption;
      }
      @Override
      public void enter () {
         getContext().getCheckersUI().triggerNextStateTimer(this);
         currentAgentResponse = getContext().getCheckersUI()
               .getCurrentAgentResponse(humanChosenCm);
         CheckersClient.gazeDirection = "sayandgazeresp";
      }
      @Override
      public void goToNextState () {
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
               return new endOfPlugin(context);
            }
         });
      }
      @Override 
      public void enter(){
         CheckersClient.gazeDirection = "";
         //CheckersClient.gazeDirection = "useronce";
         CheckersClient.gameOver = true;
         getContext().getCheckersUI().makeBoardUnplayable();
         getContext().getCheckersUI().updatePlugin(this);
      }
   }
   
   public static class endOfPlugin extends CheckersAdjacencyPairImpl {
      public endOfPlugin(final CheckersStateContext context){
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
   
   public static String getCurrentAgentResponse () {
      return currentAgentResponse;
   }

}
