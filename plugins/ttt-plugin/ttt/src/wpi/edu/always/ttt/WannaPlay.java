package wpi.edu.always.ttt;

import java.util.List;
import java.util.Random;

import edu.wpi.disco.rt.menu.*;

public class WannaPlay extends TTTAdjacencyPairImpl {

   private static final int HUMAN_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   private static String currentComment = "";
   private static List<String> humanCommentOptions;

   public WannaPlay(final TTTStateContext context) {
      super("Do you want to play Tic Tac Toe?", context);
      choice("Sure!", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new WhoPlaysFirst(context);
         }
      });
      choice("Not really", new DialogStateTransition() {
         @Override
         public AdjacencyPair run () {
            return new WannaPlay(context);
         }
      });
   }

   public static class WhoPlaysFirst extends TTTAdjacencyPairImpl {
      public WhoPlaysFirst(final TTTStateContext context){
         super("Do you want to play the first move or should I?", context);
         getContext().getTTTUI().startPluginForTheFirstTime(this);
         choice("Let me play first", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new Limbo(context);
            }
         });
         choice("You go ahead!", new DialogStateTransition() {
            @Override
            public AdjacencyPair run () {
               return new AgentPlays(context);
            }
         });
      }
      @Override
      public void afterLimbo() {
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
   }

   //Limbo as waiting for user move
   public static class Limbo extends TTTAdjacencyPairImpl { 
      public Limbo(final TTTStateContext context){
         super(currentComment, context);
         getContext().getTTTUI().updatePlugin(this);
         getContext().getTTTUI().makeBoardPlayable();
      }
      @Override
      public void afterLimbo() {
         skipTo(new CreateCommentsAfterLimbo(getContext()));
      }
      @Override
      public void enter() {
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

   public static class AgentPlays extends TTTAdjacencyPairImpl {
      public AgentPlays(final TTTStateContext context){
         super("", context);
      }
      @Override
      public void enter(){
         getContext().getTTTUI().prepareMoveAndComment();
         getContext().getTTTUI().playAgentMove(this);
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
         if (playerIdentifier == AGENT_IDENTIFIER)
            skipTo(new Limbo(getContext()));
         else
            skipTo(new AgentPlays(getContext()));
      }
   }

   public static class HumanComments extends TTTAdjacencyPairImpl {
      public HumanComments(final TTTStateContext context, final int playerIdentifier){
         super("", context);
         for(String eachCommentOption : humanCommentOptions)
            choice(eachCommentOption, new DialogStateTransition() {
               @Override
               public AdjacencyPair run () {
                  if (playerIdentifier == AGENT_IDENTIFIER)
                     return new Limbo(getContext());
                  else
                     return new AgentPlays(getContext());
               }
            });
      }
   }



}
