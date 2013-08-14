package wpi.edu.always.ttt;

import java.util.List;
import java.util.Random;

import edu.wpi.disco.rt.menu.*;

public class WhoPlaysFirst extends TTTAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static String currentComment = "";
   private static List<String> humanCommentOptions;
   private static String WhatAgentSaysIfHumanDoesNotChooseAComment = "";
   private static boolean agentIsCommenting = false;

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
         choice("test", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               TTTClient.gameOver = true;
               return new gameOver(context);
            }
         });
   }
   @Override
   public void enter() {
      if(!TTTClient.gameOver)
         getContext().getTTTUI().startPluginForTheFirstTime(this);
      else {
         TTTClient.gameOver = false;
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().resetGame();
      }
   }
   
   //Limbo as waiting for user move
   public static class Limbo extends TTTAdjacencyPairImpl { 
      public Limbo(final TTTStateContext context){
         super(currentComment, context);
      }
      @Override
      public void enter() {
         if(TTTClient.gameOver)
            skipTo(new gameOver(getContext()));
         if(!agentIsCommenting)
            TTTClient.gazeLeft = true;
         else 
            TTTClient.gazeBack = true;
         getContext().getTTTUI().makeBoardPlayable();
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().
            triggerAgentCommentOnUserTurnGazeDelay();
      }
      @Override
      public void afterLimbo() {
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      protected void afterAgentCommentOnUserTurnGazeDelayOver() {
         TTTClient.gazeLeft = true;
      }
   }
   
   public static class CreateCommentsAfterLimbo extends TTTAdjacencyPairImpl { 
      public CreateCommentsAfterLimbo(final TTTStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         getContext().getTTTUI().makeBoardUnplayable();
         currentComment = getContext().getTTTUI().getCurrentAgentComment();
         humanCommentOptions = getContext().getTTTUI().getCurrentHumanCommentOptions();
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
         if(TTTClient.gameOver)
            skipTo(new gameOver(getContext()));
         agentIsCommenting = false;
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
         getContext().getTTTUI().prepareAgentComment();
         currentComment = getContext().getTTTUI().getCurrentAgentComment();
         humanCommentOptions = getContext().getTTTUI().getCurrentHumanCommentOptions();
         if(new Random().nextBoolean())
            skipTo(new AgentComments(getContext(), AGENT_IDENTIFIER));
         else
            skipTo(new HumanComments(getContext(), AGENT_IDENTIFIER));
      }
   }

   public static class AgentComments extends TTTAdjacencyPairImpl {
      int playerIdentifier;
      public AgentComments(final TTTStateContext context, final int playerIdentifier){
         super("", context);
         this.playerIdentifier = playerIdentifier;
      }
      @Override 
      public void enter(){
         agentIsCommenting = true;
         TTTClient.gazeBack = true;
         if (playerIdentifier == AGENT_IDENTIFIER){
            getContext().getTTTUI().prepareAgentComment();
            currentComment = getContext().getTTTUI().getCurrentAgentComment();
            skipTo(new Limbo(getContext()));
         }
         else
            skipTo(new AgentPlayDelay(getContext()));
      }
   }

   public static class HumanComments extends TTTAdjacencyPairImpl {
      int playerIdentifier;
      public HumanComments(final TTTStateContext context, final int playerIdentifier){
         super("", context);
         this.playerIdentifier = playerIdentifier;
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  getContext().getTTTUI().cancelHumanCommentingTimer();
                  if (playerIdentifier == AGENT_IDENTIFIER)
                     return new Limbo(getContext());
                  else
                     return new AgentPlayDelay(getContext());
               }
            });
      }
      @Override
      public void afterTimeOut() {
         if (playerIdentifier == HUMAN_IDENTIFIER){
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
         currentComment = "";
         TTTClient.gazeBack = true;
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().triggerHumanCommentingTimer();
         getContext().getTTTUI().makeBoardPlayable();
      }
   }
   public static class gameOver extends TTTAdjacencyPairImpl {
      public gameOver(final TTTStateContext context){
         super("Do you want to play again?", context);
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
         TTTClient.gazeBack = true;
         getContext().getTTTUI().makeBoardUnplayable();
         getContext().getTTTUI().updatePlugin(this);
         currentComment = "";
      }
   }

}
