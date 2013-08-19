package wpi.edu.always.ttt;

import java.util.List;
import java.util.Random;

import edu.wpi.disco.rt.menu.*;

public class WhoPlaysFirst extends TTTAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static String currentAgentComment = "";
   private static List<String> humanCommentOptions;
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";
   private static boolean agentIsCommenting = false;
   static boolean agentIsCommentingOnUserMove = false;

   //   public WannaPlay(final TTTStateContext context) {
   //      super("Do you want to play Tic Tac Toe?", context);
   //      choice("Sure", new DialogStateTransition() {
   //         @Override
   //         public AdjacencyPair run () {
   //            return new WhoPlaysFirst(context);
   //         }
   //      });
   //      choice("Not really", new DialogStateTransition() {
   //         @Override
   //         public AdjacencyPair run () {
   //            return new WannaPlay(context);
   //         }
   //      });
   //   }

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
         super(currentAgentComment, context);
      }
      @Override
      public void enter() {
         if(TTTClient.gameOver){
            humanCommentOptions = getContext().getTTTUI()
                  .getCurrentHumanCommentOptionsForAMoveBy(
                        AGENT_IDENTIFIER);
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         if(!agentIsCommenting)
            TTTClient.gazeLeft = true;
         else 
            TTTClient.gazeBack = true;
         getContext().getTTTUI().makeBoardPlayable();
         getContext().getTTTUI().updatePlugin(this);
      }
      @Override
      public void afterLimbo() {
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
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment();
            skipTo(new gameOverDialogue(getContext()));
         }
         agentIsCommenting = false;
         if(agentIsCommentingOnUserMove){
            TTTClient.sayAgentCommentOnHumanMove = true; 
            agentIsCommentingOnUserMove =  false;
         }
         else
            TTTClient.gazeUpLeft = true;
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().triggerAgentPlayTimer();
         getContext().getTTTUI().prepareAgentMove();
      }
      @Override
      public void afterAgentPlayDelay() {
         skipTo(new AgentPlays(getContext()));
      }
   }

   public static class AgentPlays extends TTTAdjacencyPairImpl {
      public AgentPlays(final TTTStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         TTTClient.gazeLeft = true;
         getContext().getTTTUI().playAgentMove(this);
         getContext().getTTTUI().getCurrentAgentCommentForAMoveBy(
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
         super("", context);
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         agentIsCommenting = true;
         TTTClient.gazeBack = true;
         if (playerIdentifier == AGENT_IDENTIFIER){
            getContext().getTTTUI().getCurrentAgentCommentForAMoveBy(
                  playerIdentifier);
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment() + 
                  "<GAZE horizontal=\"-2\" vertical=\"-1\"/>";
            skipTo(new Limbo(getContext()));
         }
         else{
            currentAgentComment = getContext().getTTTUI()
                  .getCurrentAgentComment();
            //***limbo>agentComments>>CM?>>AgentPlayDelay>>Play>>CM 
            agentIsCommentingOnUserMove = true;
            skipTo(new AgentPlayDelay(getContext()));
         }
      }
   }

   public static class HumanComments extends TTTAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final TTTStateContext context
            , final int playerIdentifier){
         super("", context);
         this.playerIdentifier = playerIdentifier;
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  //getContext().getTTTUI().cancelHumanCommentingTimer();
                  if (playerIdentifier == AGENT_IDENTIFIER)
                     return new Limbo(getContext());
                  else
                     return new AgentPlayDelay(getContext());
               }
            });
         if(playerIdentifier == HUMAN_IDENTIFIER){
            choice("Just play", new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  //getContext().getTTTUI().cancelHumanCommentingTimer();
                  if (playerIdentifier == AGENT_IDENTIFIER)
                     return new Limbo(getContext());
                  else
                     return new AgentPlayDelay(getContext());
               }
            });
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
         TTTClient.gazeLeft = true;
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void enter() {
         currentAgentComment = "";
         TTTClient.gazeBack = true;
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
         super("Game over. " + currentAgentComment, context);
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override

               public AdjacencyPair run () {
                  getContext().getTTTUI().cancelHumanCommentingTimer();
                  return new gameOver(getContext());
               }
            });
         choice("Anyway", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               getContext().getTTTUI().cancelHumanCommentingTimer();
               return new gameOver(getContext());
            }
         });
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         TTTClient.gazeBack = true;
         humanCommentOptions = getContext().getTTTUI()
               .getCurrentHumanCommentOptionsForAMoveBy(HUMAN_IDENTIFIER);
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().triggerHumanCommentingTimer();
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
               return new gameOver(context);
            }
         });
      }
      @Override 
      public void enter(){
         currentAgentComment = "";
         TTTClient.gazeBack = true;
         TTTClient.gameOver = true;
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
      }
   }

   public static String getCurrentAgentComment(){
      if(currentAgentComment == null)
         return "";
      return currentAgentComment;
   }

}
