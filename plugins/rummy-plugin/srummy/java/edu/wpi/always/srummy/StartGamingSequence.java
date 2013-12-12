package edu.wpi.always.srummy;

import java.util.List;
import java.util.Random;
import edu.wpi.disco.rt.menu.*;

public class StartGamingSequence extends SrummyAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static List<String> humanCommentOptions;
   private static String currentAgentComment = "";
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";
   private static boolean receivedAgentDrawOptions = false;
   private static boolean receivedAgentDiscardOptions = false;
   private static boolean receivedAgentMeldOptions = false;
   private static boolean receivedAgentLayoffOptions = false;

   public StartGamingSequence(final SrummyStateContext context) {
      super("Let's play rummy", context);
      System.out.println(">>>> StartGamingSequence");
      choice("Ok", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new Limbo(context);
         }
      });
   }
   @Override
   public void enter() {
      if(!SrummyClient.gameOver)
         getContext().getSrummyUI().startPluginForTheFirstTime(this);
      else {
         SrummyClient.gameOver = false;
         getContext().getSrummyUI().resetGame();
         getContext().getSrummyUI().updatePlugin(this);
      }
//      SrummyClient.gazeDirection = "board";
      //getContext().getSrummyUI().makeBoardPlayable();
   }
   @Override
   public void humanMoveReceived () {
      currentAgentComment = "";
      skipTo(new CreateCommentsAfterLimbo(getContext()));
   }
   @Override
   public void agentMoveOptionsReceived (String chosenMoveType) {
      receivedAgentDrawOptions = true;
   }

   //Limbo as waiting for user move
   public static class Limbo extends SrummyAdjacencyPairImpl { 
      public Limbo(final SrummyStateContext context){
         super("", context);
         System.out.println(">>>> Limbo");
      }
      @Override
      public void enter() {
         if(SrummyClient.gameOver){
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(
                        AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         //getContext().getSrummyUI().makeBoardPlayable();
         getContext().getSrummyUI().updatePlugin(this);
         //SrummyClient.gazeDirection = "board";
         SrummyClient.oneMeldInAgentTurnAlready = false;
         SrummyClient.oneLayoffInAgentTurnAlready = false;
         SrummyClient.limboEnteredOnce = false;
         SrummyClient.gazeDirection = "sayandgazelimbo";
      }
      @Override
      public void humanMoveReceived () {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         receivedAgentDrawOptions = true;
      }
   }

   public static class CreateCommentsAfterLimbo extends SrummyAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final SrummyStateContext context){
         super("", context);
         System.out.println(">>>> CreateCommentsAfterLimbo");
      }
      @Override
      public void enter(){
         getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
               HUMAN_IDENTIFIER);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), HUMAN_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), HUMAN_IDENTIFIER));
      }
   }

   public static class AgentPlayDelay extends SrummyAdjacencyPairImpl {
      public AgentPlayDelay(final SrummyStateContext context){
         super(WhatAgentSaysIfHumanDoesNotChooseAComment, context);
         System.out.println(">>>> AgentPlayDelay");
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
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         //getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().triggerAgentPlayTimers();
      }
      @Override
      protected void aferAgentDrawDelay(){
         if(receivedAgentDrawOptions){
            //draw, cached from before
            receivedAgentDrawOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
      }
      @Override
      protected void afterAgentPlayingGazeDelay () {
         SrummyClient.gazeDirection = "thinking";
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if(chosenMoveType.equals("discard"))
            receivedAgentDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedAgentMeldOptions = true;
         else if(chosenMoveType.equals("layoff"))
            receivedAgentLayoffOptions = true;
      }
      @Override
      public void afterDrawAfterGazeAfterThinkingDelay() {
         //got meld or discard or lay-off
         if(receivedAgentDiscardOptions 
               || receivedAgentMeldOptions 
               || receivedAgentLayoffOptions){
            skipTo(new AgentPlays(getContext()));
         }
         else
            //should have the move options by now, if not, loop
            getContext().getSrummyUI().triggerAgentPlayTimers();
      }
   }

   public static class AgentPlays extends SrummyAdjacencyPairImpl {
      public AgentPlays(final SrummyStateContext context){
         super("", context);
         System.out.println(">>>> AgentPlays");
      }
      @Override
      public void enter(){
         SrummyClient.gazeDirection = "board";
         getContext().getSrummyUI().updatePlugin(this);
         getContext().getSrummyUI().triggerAgentDiscardDelay();
      }
      @Override
      protected void afterAgentDiscardDelay () {
         if(receivedAgentDiscardOptions && !receivedAgentMeldOptions
               && !receivedAgentLayoffOptions){
            receivedAgentDiscardOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            if(new Random().nextBoolean())
               skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
            else
               skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
         }
         if(receivedAgentMeldOptions){
            receivedAgentMeldOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
         if(receivedAgentLayoffOptions){
            receivedAgentLayoffOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
      }
      @Override
      public void receivedAgentMoveOptions (String moveType) {
         if(moveType.equals("discard")){
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().sendBackAgentMove();//discard
            SrummyClient.meldedAlready = false;
            SrummyClient.agentDrawn = false;
            SrummyClient.twoMeldsInARowByAgent = false;
            if(new Random().nextBoolean())//only discard can conclude a turn
               skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
            else
               skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
         }
         else if (moveType.equals("layoff")){
            getContext().getSrummyUI().sendBackAgentMove();//lay off
         }
         else if (moveType.equals("meld")){
            SrummyClient.twoMeldsInARowByAgent = true;
            getContext().getSrummyUI().sendBackAgentMove();//meld
         }
      }
   }

   public static class AgentComments extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      public AgentComments(final SrummyStateContext context
            , final int playerIdentifier){
         super("", context);
         System.out.println(">>>> AgentComments");
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         if(!SrummyClient.gameOver){
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            getContext().getSrummyUI().updatePlugin(this);
            getContext().getSrummyUI().triggerNextStateTimer();
            SrummyClient.gazeDirection = "sayandgaze";
         }
         else{
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
            SrummyClient.gazeDirection = "board";
         }
      }
      @Override
      public void goToNextState () {
         if(playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else
            skipTo(new AgentPlayDelay(getContext()));
      }
   }

   public static class HumanComments extends SrummyAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final SrummyStateContext context
            , final int playerIdentifier){
         super("", context);
         System.out.println(">>>> HumanComments");
         SrummyClient.gazeDirection = "useronce";
         this.playerIdentifier = playerIdentifier;
         if(!SrummyClient.gameOver){
            for(String eachCommentOption : humanCommentOptions)
               choice(eachCommentOption, new DialogStateTransition() {
                  @Override
                  public AdjacencyPair run () {
                     //getContext().getSrummyUI().cancelHumanCommentingTimer();
                     if (playerIdentifier == AGENT_IDENTIFIER)
                        return new Limbo(getContext());
                     return new AgentPlayDelay(getContext());
                  }
               });
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
         SrummyClient.gazeDirection = "board";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void enter() {
         SrummyClient.gazeDirection = "useronce";
         if(SrummyClient.gameOver){
            skipTo(new gameOverDialogue(getContext()));
            SrummyClient.gazeDirection = "board";
         }
         currentAgentComment = "";
         //SrummyClient.gazeDirection = "user";
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsForAMoveBy(playerIdentifier);
         getContext().getSrummyUI().updatePlugin(this);
         //getContext().getSrummyUI().triggerHumanCommentingTimer();
         //if(playerIdentifier == AGENT_IDENTIFIER)
         //     getContext().getSrummyUI().makeBoardPlayable();
      }
   }

   public static class gameOverDialogue extends SrummyAdjacencyPairImpl {
      public gameOverDialogue(final SrummyStateContext context){
         super("", context);
         System.out.println(">>>> gameOverDialogue");
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override

               public AdjacencyPair run () {
                  //getContext().getSrummyUI().cancelHumanCommentingTimer();
                  return new gameOver(getContext());
               }
            });
         choice("Anyway", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               //getContext().getSrummyUI().cancelHumanCommentingTimer();
               return new gameOver(getContext());
            }
         });
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         SrummyClient.gazeDirection = "sayandgazegameover";
         humanCommentOptions = getContext().getSrummyUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         //getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().updatePlugin(this);
         //getContext().getSrummyUI().triggerHumanCommentingTimer();
      }
      @Override
      public void afterTimeOut() {
         skipTo(new gameOver(getContext()));
      }
   }

   public static class gameOver extends SrummyAdjacencyPairImpl {
      public gameOver(final SrummyStateContext context){
         super("Now do you want to play again?", context);
         System.out.println(">>>> gameOver");
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
         //getContext().getSrummyUI().makeBoardUnplayable();
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

}
