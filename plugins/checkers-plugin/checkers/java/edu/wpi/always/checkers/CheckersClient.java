package edu.wpi.always.checkers;

import java.util.*;
import com.google.gson.JsonObject;
import edu.wpi.always.checekrs.logic.*;
import edu.wpi.always.client.*;
import edu.wpi.sgf.scenario.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.sgf.comment.CommentingManager;
import edu.wpi.sgf.logic.AnnotatedLegalMove;

public class CheckersClient implements CheckersUI {

   private static final String PLUGIN_NAME = "checkers";
   private static final String MSG_HUMAN_MOVE = "checkers.human_move";
   private static final String MSG_AGENT_MOVE = "tictactoe.agent_move";
   private static final String MSG_BOARD_PLAYABILITY = "tictactoe.playability";

   private static final int HUMAN_COMMENTING_TIMEOUT = 15;
   private static final int AGENT_PLAY_DELAY_AMOUNT = 3;
   private static final int AGENT_PLAYING_GAZE_DELAY_AMOUNT = 2;

   public static String gazeDirection = "";
   
   public static boolean nod = false;
   public static boolean gameOver = false;
   private static final int HUMAN_IDENTIFIER = 1;

   // 1: userWins, 2: agentWins, 3: tie
   private int winOrTie = 0;

   private String currentComment;
   private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;
   private final ClientProxy proxy;
   private CheckersUIListener listener;

   private CheckersLegalMoveGenerator moveGenerator;
   private CheckersLegalMoveAnnotator moveAnnotator;
   private ScenarioManager scenarioManager;
   // private ScenarioFilter scenarioFilter;
   private MoveChooser moveChooser;
   private CommentingManager commentingManager;
   private CheckersGameState gameState;

   private Timer humanCommentingTimer;
   private Timer agentPlayDelayTimer;
   private Timer agentPlayingGazeDelayTimer;
   private Timer nextStateTimer;

   private CheckersAnnotatedLegalMove latestAgentMove;
   private CheckersAnnotatedLegalMove latestHumanMove;

   public CheckersClient (ClientProxy proxy, UIMessageDispatcher dispatcher) {
      this.proxy = proxy;
      this.dispatcher = dispatcher;
      // startPlugin(dispatcher);
      moveGenerator = new CheckersLegalMoveGenerator();
      moveAnnotator = new CheckersLegalMoveAnnotator();
      commentingManager = new CheckersCommentingManager();
      // scenarioFilter = new ScenarioFilter();
      moveChooser = new MoveChooser();
      scenarioManager = new ScenarioManager();
      gameState = new CheckersGameState();

      dispatcher.registerReceiveHandler(MSG_HUMAN_MOVE, new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            if ( listener != null ) {
               String moveDesc = body.get("moveDesc").getAsString();
               
               CheckersLegalMove humanMove = new CheckersLegalMove(
                     Integer.parseInt(moveDesc.split("//")[0].split(",")[0]), 
                     Integer.parseInt(moveDesc.split("//")[0].split(",")[1]), 
                     Integer.parseInt(moveDesc.split("//")[1].split(",")[0]), 
                     Integer.parseInt(moveDesc.split("//")[1].split(",")[1]));
               
               gameState.performUserMove(humanMove);
               
               latestHumanMove = (CheckersAnnotatedLegalMove) moveAnnotator
                     .annotate(humanMove, gameState);
               updateWin();
               if ( winOrTie > 0 )
                  makeBoardUnplayable();
               listener.humanPlayed();
            }
         }
      });

   }

   @Override
   public void playAgentMove (CheckersUIListener listener) {

      this.listener = listener;
      show();
      if ( currentMove == null )
         return;

      scenarioManager.tickAll();
      latestAgentMove = 
            (CheckersAnnotatedLegalMove) currentMove;
      gameState.performAgentMove(
            (CheckersLegalMove)currentMove.getMove());
      
      Message msg = Message
            .builder(MSG_AGENT_MOVE)
            .add("moveDesc",
                  (gameState.makeMoveDesc(
                        (CheckersLegalMove) currentMove.getMove())))
            .build();
      
      dispatcher.send(msg);
      updateWin();
   }

   @Override
   public void resetGame () {
      Message msg = Message.builder(MSG_AGENT_MOVE).add("moveDesc", "reset")
            .build();
      gameState.resetGame();
      dispatcher.send(msg);
   }

   @Override
   public String getCurrentAgentComment () {
      return currentComment;
   }

   @Override
   public List<String> getCurrentHumanCommentOptionsForAMoveBy (int player) {
      updateWin();

      if ( player == HUMAN_IDENTIFIER )
         return commentingManager.getHumanCommentingOptionsForHumanMove(
               gameState, latestHumanMove,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestHumanMove.getMove(), player));
      else
         return commentingManager.getHumanCommentingOptionsForAgentMove(
               gameState, latestAgentMove,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestAgentMove.getMove(), player));
   }

   @Override
   public void prepareAgentMove () {
      currentMove = null;
      currentMove =
      // moveChooser.choose(scenarioFilter.filter(
      // moveAnnotator.annotate(moveGenerator.generate(
      // gameState), gameState), scenarioManager.getCurrentScenario()));
      moveChooser.choose(moveAnnotator.annotate(
            moveGenerator.generate(gameState), gameState));
      updateWin();
   }

   @Override
   public void prepareAgentCommentForAMoveBy (int player) {

      updateWin();

      // null passed for scenarios here.
      if ( player == HUMAN_IDENTIFIER )
         currentComment = commentingManager.getAgentCommentForHumanMove(
               gameState, latestHumanMove, null,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestHumanMove.getMove(), player));
      else
         currentComment = commentingManager.getAgentCommentForAgentMove(
               gameState, latestAgentMove, null,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestAgentMove.getMove(), player));

      if ( currentComment == null )
         currentComment = "";

   }

   // user commenting timer
   // (used only when agent turn)
   @Override
   public void triggerHumanCommentingTimer () {
      humanCommentingTimer = new Timer();
      humanCommentingTimer.schedule(new HumanCommentingTimerSetter(),
            1000 * HUMAN_COMMENTING_TIMEOUT);
   }

   @Override
   public void cancelHumanCommentingTimer () {
      humanCommentingTimer.cancel();
      humanCommentingTimer.purge();
   }

   private class HumanCommentingTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.humanCommentTimeOut();
      }
   }

   // agent playing delay timers
   @Override
   public void triggerAgentPlayTimer () {
      agentPlayDelayTimer = new Timer();
      agentPlayingGazeDelayTimer = new Timer();
      agentPlayDelayTimer.schedule(
            new AgentPlayDelayTimerSetter(),
            1000 * AGENT_PLAY_DELAY_AMOUNT);
      agentPlayingGazeDelayTimer.schedule(
            new AgentPlayingGazeDelayTimerSetter(),
            1000 * AGENT_PLAYING_GAZE_DELAY_AMOUNT);
   }

   private class AgentPlayDelayTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.agentPlayDelayOver();
      }
   }
   
   private class AgentPlayingGazeDelayTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.agentPlayingGazeDelayOver();
      }
   }
   
   @Override
   public void triggerNextStateTimer (
         CheckersUIListener listener) {
      nextStateTimer = new Timer();
      nextStateTimer.schedule(new NextStateTimerSetter(),
            4000);
   }
   private class NextStateTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.nextState();
      }
   }
   
   private void updateWin () {

      if ( gameState.possibleWinner() != 0 )
         CheckersClient.gameOver = true;
      
   }

   public void show () {
      proxy.startPlugin(PLUGIN_NAME, InstanceReuseMode.Reuse, null);
   }

   @Override
   public void startPluginForTheFirstTime (CheckersUIListener listener) {
      updatePlugin(listener);
   }

   @Override
   public void updatePlugin (CheckersUIListener listener) {
      this.listener = listener;
      show();
   }

   @Override
   public void makeBoardPlayable () {
      if ( gameState.possibleWinner() == 0 ) {
         Message m = Message.builder(MSG_BOARD_PLAYABILITY)
               .add("value", "true").build();
         dispatcher.send(m);
      }
   }

   @Override
   public void makeBoardUnplayable () {
      Message m = Message.builder(MSG_BOARD_PLAYABILITY).add("value", "false")
            .build();
      dispatcher.send(m);
   }

}
