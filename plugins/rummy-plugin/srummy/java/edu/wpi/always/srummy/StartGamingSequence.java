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
   private static boolean receivedDrawOptions = false;
   private static boolean receivedDiscardOptions = false;
   private static boolean receivedMeldOptions = false;

   public StartGamingSequence(final SrummyStateContext context) {
      super("", context);
      System.out.println(">>>> StartGamingSequence");
      //      super("Do you want to play the first move or should I?", context);
      //      choice("Let me play first", new DialogStateTransition() {
      //         @Override
      //         public AdjacencyPair run () {
      //            return new Limbo(context);
      //         }
      //      });
      //      choice("You go ahead", new DialogStateTransition() {
      //         @Override
      //         public AdjacencyPair run () {
      //            return new AgentPlayDelay(context);
      //         }
      //      });
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
      SrummyClient.gazeDirection = "board";
      //getContext().getSrummyUI().makeBoardPlayable();
   }
   @Override
   public void humanMoveReceived () {
      currentAgentComment = "";
      skipTo(new CreateCommentsAfterLimbo(getContext()));
   }
   @Override
   public void agentMoveOptionsReceived (String chosenMoveType) {
      receivedDrawOptions = true;
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
         SrummyClient.gazeDirection = "sayandgazelimbo";
      }
      @Override
      public void humanMoveReceived () {
         currentAgentComment = "";
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         receivedDrawOptions = true;
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
         if(receivedDrawOptions){
            //draw, cached from before
            receivedDrawOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
         if(SrummyClient.gameOver){
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         SrummyClient.gazeDirection = "board";
         //getContext().getSrummyUI().makeBoardUnplayable();
         getContext().getSrummyUI().updatePlugin(this);
         getContext().getSrummyUI().triggerAgentPlayTimer();
      }
      @Override
      protected void afterAgentPlayingGazeDelay () {
         SrummyClient.gazeDirection = "thinking";
      }
      @Override
      public void agentMoveOptionsReceived (String chosenMoveType) {
         if(chosenMoveType.equals("discard"))
            receivedDiscardOptions = true;
         else if(chosenMoveType.equals("meld"))
            receivedMeldOptions = true;
//         else if (layoff) //add tomo
//            ...
      }
      @Override
      public void afterDrawAfterThinkingDelay() {
         //got meld or discard
         if(receivedDiscardOptions || receivedMeldOptions){
            skipTo(new AgentPlays(getContext()));
         }
         else
            //should have the move options by now, if not, loop
            getContext().getSrummyUI().triggerAgentPlayTimer();
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

         if(receivedDiscardOptions && !receivedMeldOptions){
            receivedDiscardOptions = false;
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
         if(receivedMeldOptions){
            receivedMeldOptions = false;
            getContext().getSrummyUI().sendBackAgentMove();
         }
      }
      @Override
      public void receivedAgentMoveOptions (String moveType) {
         //can be only meld or discard
//         if(moveType.equals("meld")){
//            getContext().getSrummyUI().sendBackAgentMove();//meld
//         }
         if(moveType.equals("discard")){ //only this case left
            getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
                  AGENT_IDENTIFIER);
            currentAgentComment = getContext().getSrummyUI()
                  .getCurrentAgentComment();
            humanCommentOptions = getContext().getSrummyUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(AGENT_IDENTIFIER);
            getContext().getSrummyUI().sendBackAgentMove();//discard
            if(new Random().nextBoolean())
               skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
            else
               skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
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
         getContext().getSrummyUI().prepareAgentCommentForAMoveBy(
               playerIdentifier);
         currentAgentComment = getContext().getSrummyUI()
               .getCurrentAgentComment();
         getContext().getSrummyUI().updatePlugin(this);
         getContext().getSrummyUI().triggerNextStateTimer();
         SrummyClient.gazeDirection = "sayandgaze";
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
                     else
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
                     else
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
               return new gameOver(context);
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

   public static String getCurrentAgentComment () {
      return currentAgentComment;
   }

}
