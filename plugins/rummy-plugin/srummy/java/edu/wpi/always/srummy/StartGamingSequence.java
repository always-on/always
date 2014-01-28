package edu.wpi.always.srummy;

import java.util.ArrayList;
import java.util.List;
import edu.wpi.disco.rt.menu.*;

public class StartGamingSequence extends SrummyAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static List<String> humanCommentOptions;
   private static String currentAgentComment = "";
   private static String currentAgentResponse = "";
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";
   private static boolean receivedAgentDrawOptions = false;
   private static boolean receivedAgentDiscardOptions = false;
   private static boolean receivedAgentMeldOptions = false;
   private static boolean receivedAgentLayoffOptions = false;
   private static List<String> humanResponseOptions = 
         new ArrayList<String>();

   public StartGamingSequence(final SrummyStateContext context) {
      super("Ok, do you want to play first or should I?", context);
      System.out.println("\n>>>> StartGamingSequence");
      choice("I'll go first", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            getContext().getSrummyUI().setUpGame(HUMAN_IDENTIFIER);
            return new Limbo(context);
         }
      });
      choice("You go ahead", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            getContext().getSrummyUI().setUpGame(AGENT_IDENTIFIER);
            return new AgentPlayDelay(context);
         }
      });
   }
   @Override
   public void enter() {
      if(SrummyClient.gameOver){
         SrummyClient.gameOver = false;
         getContext().getSrummyUI().reset();
         getContext().getSrummyUI().updatePlugin(this);
//         getContext().getSrummyUI().resetGame();
      }
//      getContext().getSrummyUI().makeBoardPlayable();
//      SrummyClient.gazeDirection = "boardonce";
   }
   @Override
   public void humanMoveReceived () {
      currentAgentComment = "";
      skipTo(new CreateCommentsAfterLimbo(getContext()));
   }
   @Override
   public void agentMoveOptionsReceived (String chosenMoveType) {
      if (chosenMoveType.equals("draw"))
         receivedAgentDrawOptions = true;
      else if(chosenMoveType.equals("discard"))
         receivedAgentDiscardOptions = true;
      else if(chosenMoveType.equals("meld"))
         receivedAgentMeldOptions = true;
      else if(chosenMoveType.equals("layoff"))
         receivedAgentLayoffOptions = true;
   }

   //Limbo as waiting for user move
   public static class Limbo extends SrummyAdjacencyPairImpl { 
      public Limbo(final SrummyStateContext context){
         super("", context);
         System.out.println("\n>>>> Limbo");
      }
      @Override
      public void enter() {
         if(SrummyClient.gameOver){
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
            humanResponseOptions.addAll(getContext().getSrummyUI()
                  .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getSrummyUI().makeBoardPlayable();
            getContext().getSrummyUI().updatePlugin(this);
            //SrummyClient.gazeDirection = "board";
            SrummyClient.oneMeldInAgentTurnAlready = false;
            SrummyClient.oneLayoffInAgentTurnAlready = false;
            SrummyClient.limboEnteredOnce = false;
            SrummyClient.gazeDirection = "sayandgazelimbo";
         }
      }
      @Override
      public void humanMoveReceived () {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
      @Override
      //this method would be used only when user cards would finish as a result of 
      // a meld including the card he/she just drew. 
      //Therefore, no discard would be necessary/possible. 
      public void gameIsOverByYieldingZeroCardsInATurn () {
         getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         humanResponseOptions.clear();
         try{
            humanResponseOptions.addAll(getContext().getSrummyUI()
                  .getCurrentHumanResponseOptions());
         }catch(Exception e){/*in case no response exists*/}
         SrummyClient.gazeDirection = "";
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
         if(SrummyClient.random.nextBoolean())
            skipTo(new gameOverDialogueByAgent(getContext()));
         else
            skipTo(new gameOverDialogueByHuman(getContext()));
      }
   }

   public static class CreateCommentsAfterLimbo extends SrummyAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final SrummyStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         System.out.println("\n>>>>  CreateCommentsAfterLimbo");
         getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
               HUMAN_IDENTIFIER);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(HUMAN_IDENTIFIER);
         humanResponseOptions.clear();
         try{
         humanResponseOptions.addAll(getContext().getSrummyUI()
               .getCurrentHumanResponseOptions());
         }catch(Exception e){/*in case no response exists*/}
         if(SrummyClient.random.nextBoolean() || SrummyClient.random.nextBoolean() 
               || SrummyClient.thereAreGameSpecificTags){
            //by 75% chance (or if there is game specific comment) here: full comment exchange
            SrummyClient.thereAreGameSpecificTags = false;
            if(SrummyClient.random.nextBoolean())
               skipTo(new AgentComments(getContext(), HUMAN_IDENTIFIER));
            else
               skipTo(new HumanComments(getContext(), HUMAN_IDENTIFIER));
         }
         else{
            //by 25% chance here: no comment exchange
            skipTo(new AgentPlayDelay(getContext()));
         }
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
   }

   public static class AgentPlayDelay extends SrummyAdjacencyPairImpl {
      public AgentPlayDelay(final SrummyStateContext context){
         super(WhatAgentSaysIfHumanDoesNotChooseAComment, context);
         System.out.println("\n>>>> AgentPlayDelay");
         WhatAgentSaysIfHumanDoesNotChooseAComment = "";
      }
      @Override
      public void enter(){
         SrummyClient.twoMeldsInARowByHuman = false;
         SrummyClient.oneMeldInHumanTurnAlready = false;
         SrummyClient.oneLayoffInHumanTurnAlready = false;
         SrummyClient.gazeDirection = "board";
         getContext().getSrummyUI().updatePlugin(this);
         if(SrummyClient.gameOver){
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  HUMAN_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
            humanResponseOptions.addAll(getContext().getSrummyUI()
                  .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            getContext().getSrummyUI().makeBoardUnplayable();
            getContext().getSrummyUI().triggerAgentPlayTimers();
         }
      }
      @Override
      protected void aferAgentDrawDelay(){
         if(receivedAgentDrawOptions){
            //draw, cached from before
            receivedAgentDrawOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
         else
            getContext().getSrummyUI().waitMoreForAgentDrawOptions(this);
      }
      @Override
      protected void timesUpForDrawOption () {
         //loops till get them
         getContext().getSrummyUI().cancelUpcomingTimersTillNextRound(this);
         skipTo(new AgentPlayDelay(getContext()));
      }
      @Override
      protected void afterAgentPlayingGazeDelay () {
         SrummyClient.gazeDirection = "thinking";
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
      @Override
      public void afterAgentPlayingDelay() {
         //got meld or discard or lay-off
         if(receivedAgentDiscardOptions 
               || receivedAgentMeldOptions 
               || receivedAgentLayoffOptions){
            skipTo(new AgentPlays(getContext()));
         }
         else
            //should have the move options by now, if not, loop
            //this is after all timers, so no cancels
            //calls this method back in 2s
            getContext().getSrummyUI()
            .waitMoreForAgentDiscardMeldLayoff(this);
      }
   }

   public static class AgentPlays extends SrummyAdjacencyPairImpl {
      private static boolean secondMovesRound = false;
      public AgentPlays(final SrummyStateContext context){
         super("", context);
         System.out.println("\n>>>> AgentPlays");
      }
      @Override
      public void enter(){
         AgentPlays.secondMovesRound = false;
         SrummyClient.gazeDirection = "board";
         getContext().getSrummyUI().updatePlugin(this);
         getContext().getSrummyUI().triggerAgentDiscardOrMeldLayoffDelay();
      }
      @Override
      protected void afterAgentDiscardOrMeldLayoffDelay () {
         if(receivedAgentDiscardOptions && !receivedAgentMeldOptions
               && !receivedAgentLayoffOptions){
            receivedAgentDiscardOptions = false;
            AgentPlays.secondMovesRound = false;
            
            SrummyClient.meldedOnce = false;
            SrummyClient.agentDrew = false;
            SrummyClient.twoMeldsInARowByAgent = false;
            
            getContext().getSrummyUI().sendBackAgentMove();
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            //only a discard would conclude a turn...
            if(SrummyClient.random.nextBoolean() || SrummyClient.random.nextBoolean() 
                  || SrummyClient.thereAreGameSpecificTags){
               //by 75% chance (or if there is game specific comment) here: full comment exchange
               SrummyClient.thereAreGameSpecificTags = false;
               if(SrummyClient.random.nextBoolean())
                  skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
               else
                  skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
            }
            else{
               //by 25% chance here: no comment exchange
               skipTo(new Limbo(getContext()));
            }
         }
         else{
            if(receivedAgentMeldOptions){
               receivedAgentMeldOptions = false;
               getContext().getSrummyUI().sendBackAgentMove();
               if(AgentPlays.secondMovesRound && SrummyClient.meldedOnce)
                  SrummyClient.twoMeldsInARowByAgent = true;
               SrummyClient.meldedOnce = true;
            }
            else if(receivedAgentLayoffOptions){
               receivedAgentLayoffOptions = false;
               getContext().getSrummyUI().sendBackAgentMove();
            }
         }
      }
      @Override
      public void agentMoveOptionsReceived (String moveType) {
         AgentPlays.secondMovesRound = true;
         if(moveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(moveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(moveType.equals("layoff"))
            receivedAgentLayoffOptions = true;

         //if did a meld or a lay off, and wants to meld or lay off again:
         getContext().getSrummyUI().triggerAgentDiscardOrMeldLayoffDelay();
      }
      @Override
      //this method would be used only when agent cards would finish as a result of 
      // a meld including the card he(/she?!) just drew. 
      //Therefore, no discard would be necessary/possible. 
      public void gameIsOverByYieldingZeroCardsInATurn () {
         getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
               AGENT_IDENTIFIER);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         humanResponseOptions.clear();
         try{
            humanResponseOptions.addAll(getContext().getSrummyUI()
                  .getCurrentHumanResponseOptions());
         }catch(Exception e){/*in case no response exists*/}
         SrummyClient.gazeDirection = "";
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
         if(SrummyClient.random.nextBoolean())
            skipTo(new gameOverDialogueByAgent(getContext()));
         else
            skipTo(new gameOverDialogueByHuman(getContext()));
      }
   }

   public static class AgentComments extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      public AgentComments(final SrummyStateContext context
            , final int playerIdentifier){
         super("", context);
         System.out.println("\n>>>> AgentComments");
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         humanResponseOptions.clear();
         try{
            humanResponseOptions.addAll(getContext().getSrummyUI()
                  .getCurrentHumanResponseOptions());
         }catch(Exception e){/*in case no response exists*/}
         if(!SrummyClient.gameOver){
            getContext().getSrummyUI().updatePlugin(this);
            getContext().getSrummyUI().triggerNextStateTimer(this);
            SrummyClient.gazeDirection = "sayandgaze";
         }
         else{
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
      }
      @Override
      public void goToNextState () {
         skipTo(new HumanResponds(getContext(), playerIdentifier));
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
   }
   
   public static class HumanResponds extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      public HumanResponds(final SrummyStateContext context
            , final int playerIdentifier){
         super("", context);
         System.out.println("\n>>>>HumanResponses");
         SrummyClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!SrummyClient.gameOver){
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
                     //getContext().getSrummyUI().cancelHumanCommentingTimer();
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
                     //getContext().getSrummyUI().cancelHumanCommentingTimer();
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
         if(playerIdentifier == AGENT_IDENTIFIER){
            SrummyClient.gazeDirection = "board";
            skipTo(new CreateCommentsAfterLimbo(getContext()));
         }
         else //would not be here logically, robustness
            getContext().getSrummyUI().makeBoardUnplayable();
      }
      @Override
      public void enter() {
         SrummyClient.gazeDirection = "useronce";
         if(SrummyClient.gameOver){
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
               humanResponseOptions.addAll(getContext().getSrummyUI()
                     .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            currentAgentComment = "";
            getContext().getSrummyUI().updatePlugin(this);
            if(playerIdentifier == AGENT_IDENTIFIER)
               getContext().getSrummyUI().makeBoardPlayable();
            else 
               getContext().getSrummyUI().makeBoardUnplayable();
            //SrummyClient.gazeDirection = "useronce";
            //getContext().getSrummyUI().triggerHumanCommentingTimer();
         }
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
   }

   public static class HumanComments extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final SrummyStateContext context
            , final int playerIdentifier){
         super("", context);
         System.out.println("\n>>>> HumanComments");
         SrummyClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!SrummyClient.gameOver){
            for(final String eachCommentOption : humanCommentOptions){
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getSrummyUI().cancelHumanCommentingTimer();
                     return new AgentResponds(
                           getContext(), playerIdentifier, eachCommentOption);
                  }
               });
            }
            if(playerIdentifier == HUMAN_IDENTIFIER){
               choice("Your turn", new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getSrummyUI().cancelHumanCommentingTimer();
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
         if(playerIdentifier == AGENT_IDENTIFIER){
            SrummyClient.gazeDirection = "board";
            skipTo(new CreateCommentsAfterLimbo(getContext()));
         }
         else //would not be here logically, robustness
            getContext().getSrummyUI().makeBoardUnplayable();
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
      @Override
      public void enter() {
//         SrummyClient.gazeDirection = "useronce";
         if(SrummyClient.gameOver){
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
               humanResponseOptions.addAll(getContext().getSrummyUI()
                     .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
         else{
            currentAgentComment = "";
            getContext().getSrummyUI().updatePlugin(this);
            if(playerIdentifier == AGENT_IDENTIFIER)
               getContext().getSrummyUI().makeBoardPlayable();
            else 
               getContext().getSrummyUI().makeBoardUnplayable();
            //SrummyClient.gazeDirection = "useronce";
            //getContext().getSrummyUI().triggerHumanCommentingTimer();
         }
      }
   }
   
   public static class AgentResponds extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      String humanChoosenComment;
      public AgentResponds(final SrummyStateContext context
            , final int playerIdentifier, String humanChoosenComment){
         super("", context);
         System.out.println("\n>>>> AgentResponses");
         this.playerIdentifier = playerIdentifier;
         this.humanChoosenComment = humanChoosenComment;
      }
      @Override 
      public void enter(){
         getContext().getSrummyUI().makeBoardUnplayable();
         if(!SrummyClient.gameOver){
            currentAgentResponse = getContext().getSrummyUI()
                  .getCurrentAgentResponse(humanChoosenComment);
            getContext().getSrummyUI().updatePlugin(this);
            getContext().getSrummyUI().triggerNextStateTimer(this);
            SrummyClient.gazeDirection = "sayandgazeresp";
         }
         else{
            //logically should not be here, for robustness
            SrummyClient.gazeDirection = "";
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsAgentResponseForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentUserResponseForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanResponseOptions.clear();
            try{
               humanResponseOptions.addAll(getContext().getSrummyUI()
                     .getCurrentHumanResponseOptions());
            }catch(Exception e){/*in case no response exists*/}
            if(SrummyClient.random.nextBoolean())
               skipTo(new gameOverDialogueByAgent(getContext()));
            else
               skipTo(new gameOverDialogueByHuman(getContext()));
         }
      }
      @Override
      public void goToNextState () {
         if (playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else 
            skipTo(new AgentPlayDelay(getContext()));
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if (chosenMoveType.equals("draw"))
            receivedAgentDrawOptions = true;
         else if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
   }

   public static class gameOverDialogueByAgent extends SrummyAdjacencyPairImpl {
      public gameOverDialogueByAgent(final SrummyStateContext context){
         super("", context);
      }
      @Override 
      public void enter(){
         System.out.println("\n>>>gameOverDialogueByAgent\n");
         getContext().getSrummyUI().triggerNextStateTimer(this);
         getContext().getSrummyUI().makeBoardUnplayable();
         SrummyClient.gazeDirection = "sayandgaze";
      }
      @Override
      public void goToNextState () {
         skipTo(new gameOverdrbh(getContext()));
      }
   }
   
   public static class gameOverDialogueByHuman extends SrummyAdjacencyPairImpl {
      public gameOverDialogueByHuman(final SrummyStateContext context){
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
         System.out.println("\n>>>gameOverDialogueByHuman\n\n");
         currentAgentComment = "";
         getContext().getSrummyUI().makeBoardUnplayable();
      }
   }

   public static class gameOverdrbh extends SrummyAdjacencyPairImpl {
      public gameOverdrbh(final SrummyStateContext context){
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
         System.out.println("\n>>>ngameOverdrbh\n\n");
         SrummyClient.gazeDirection = "";
         SrummyClient.gameOver = true;
      }
   }
   
   public static class gameOverDialogueResponseByAgent extends SrummyAdjacencyPairImpl {
      String humanChosenCm;
      public gameOverDialogueResponseByAgent(
            final SrummyStateContext context, String eachCommentOption){
         super("", context);
         this.humanChosenCm = eachCommentOption;
      }
      @Override
      public void enter () {
         System.out.println("\n>>>gameOverDialogueResponseByAgent\n\n\n");
         getContext().getSrummyUI().triggerNextStateTimer(this);
         currentAgentResponse = getContext().getSrummyUI()
               .getCurrentAgentResponse(humanChosenCm);
         SrummyClient.gazeDirection = "sayandgazeresp";
      }
      @Override
      public void goToNextState () {
         skipTo(new gameOver(getContext()));
      }
   }

   public static class gameOver extends SrummyAdjacencyPairImpl {
      public gameOver(final SrummyStateContext context){
         super("Now do you want to play again?", context);
         System.out.println("\n>>>> gameOver");
         choice("Sure", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new StartGamingSequence(context);
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
         SrummyClient.gazeDirection = "";
         //SrummyClient.gazeDirection = "user";
         SrummyClient.gameOver = true;
         getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().updatePlugin(this);
      }
   }
   
   public static class endingPlugin extends SrummyAdjacencyPairImpl {
      public endingPlugin(final SrummyStateContext context){
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
