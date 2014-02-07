package edu.wpi.always.ttt;

import java.util.*;
import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.client.*;
import edu.wpi.always.ttt.logic.*;
import edu.wpi.sgf.comment.Comment;
import edu.wpi.sgf.comment.CommentLibraryHandler;
import edu.wpi.sgf.comment.CommentingManager;
import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.scenario.*;

public class TTTClient implements TTTUI {

   private static final String PLUGIN_NAME = "tictactoe";
   private static final String MSG_HUMAN_MOVE = "tictactoe.human_played_cell";
   private static final String MSG_AGENT_MOVE = "tictactoe.agent_cell";
   private static final String MSG_BOARD_PLAYABILITY = "tictactoe.playability";

   private static final int HUMAN_COMMENTING_TIMEOUT = 15;
   private static final int AGENT_PLAY_DELAY_AMOUNT = 5;
   private static final int AGENT_PLAYING_GAZE_DELAY_AMOUNT = 3;
   private static final int NEXT_STATE_DELAY = 3000;//ms

   public static String gazeDirection = "";
   
   public static boolean nod = false;

   public static boolean gameOver = false;

   private static final int HUMAN_IDENTIFIER = 1;

   private static final int AGENT_IDENTIFIER = 2;
   
   public static boolean thereAreGameSpecificTags = false;

   // 1: userWins, 2: agentWins, 3: tie
   private int winOrTie = 0;

   private String currentAgentComment;
   private Map<String, String> currentAgentResponseOptions;
   private List<String> currentHumanResponseOptions;
   private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;
   private final ClientProxy proxy;
   private TTTUIListener listener;

   private TTTLegalMoveGenerator moveGenerator;
   private TTTLegalMoveAnnotator moveAnnotator;
   private ScenarioManager scenarioManager;
   // private ScenarioFilter scenarioFilter;
   private MoveChooser moveChooser;
   private CommentingManager commentingManager;
   private TTTGameState gameState;

   private Timer humanCommentingTimer;
   private Timer agentPlayDelayTimer;
   private Timer agentPlayingGazeDelayTimer;
   private Timer nextStateTimer;

   private TTTAnnotatedLegalMove latestAgentMove;
   private TTTAnnotatedLegalMove latestHumanMove;
   
   public static Random random;

   public TTTClient (ClientProxy proxy, UIMessageDispatcher dispatcher) {
      this.proxy = proxy;
      this.dispatcher = dispatcher;
      // startPlugin(dispatcher);
      moveGenerator = new TTTLegalMoveGenerator();
      moveAnnotator = new TTTLegalMoveAnnotator();
      commentingManager = new TTTCommentingManager();
      // scenarioFilter = new ScenarioFilter();
      moveChooser = new MoveChooser();
      scenarioManager = new ScenarioManager();
      gameState = new TTTGameState();
      currentHumanResponseOptions = new ArrayList<String>();
      currentAgentResponseOptions = new HashMap<String, String>();
      random = new Random();
      random.setSeed(12345);
      
      dispatcher.registerReceiveHandler(MSG_HUMAN_MOVE, new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            if ( listener != null ) {
               int cellNum = Integer.valueOf(body.get("cellNum").getAsString());
               gameState.updateLastBoardState();
               gameState.board[cellNum - 1] = HUMAN_IDENTIFIER;
               latestHumanMove = (TTTAnnotatedLegalMove) moveAnnotator
                     .annotate(new TTTLegalMove(cellNum - 1), gameState);
               updateWinOrTie();
               if ( winOrTie > 0 )
                  makeBoardUnplayable();
               listener.humanPlayed();
            }
         }
      });

   }

   @Override
   public void playAgentMove (TTTUIListener listener) {

      this.listener = listener;
      show();
      if ( currentMove == null )
         return;

      scenarioManager.tickAll();

      gameState.updateLastBoardState();
      gameState.board[((TTTLegalMove) currentMove.getMove()).getCellNum()] = AGENT_IDENTIFIER;
      latestAgentMove = (TTTAnnotatedLegalMove) currentMove;
      Message msg = Message
            .builder(MSG_AGENT_MOVE)
            .add("cellNum",
                  ((TTTLegalMove) currentMove.getMove()).getCellNum() + 1)
            .build();
      dispatcher.send(msg);
      updateWinOrTie();
   }

   @Override
   public void resetGame () {
      Message msg = Message.builder(MSG_AGENT_MOVE).add("cellNum", "reset")
            .build();
      gameState.resetBoard();
      gameState.resetGameStatus();
      dispatcher.send(msg);
   }

   @Override
   public String getCurrentAgentComment () {
      return currentAgentComment;
   }
   
   @Override
   public String getCurrentAgentResponse(
         String humanChoosenComment) {
      return currentAgentResponseOptions
            .get(humanChoosenComment.trim());
   }

   @Override
   public List<String> 
   getCurrentHumanCommentOptionsAgentResponseForAMoveBy (int player) {

      updateWinOrTie();
      
      List<Comment> humanCommentingOptions = 
            new ArrayList<Comment>();
      
      if ( player == HUMAN_IDENTIFIER )
         humanCommentingOptions.addAll(commentingManager.
               getHumanCommentingOptionsAndAnAgentResponseForHumanMove(
               gameState, latestHumanMove,
               gameState.getGameSpecificCommentingTags()));
      else
         humanCommentingOptions.addAll(commentingManager.
               getHumanCommentingOptionsForAgentMove(
            gameState, latestAgentMove,
            gameState.getGameSpecificCommentingTags()));
      
      currentAgentResponseOptions.clear();
      for(Comment each : humanCommentingOptions)
         currentAgentResponseOptions.put(
               each.getContent().trim(), each.getOneResponseOption());
      
      return CommentLibraryHandler
            .getContentsOfTheseComments(humanCommentingOptions);
      
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
      updateWinOrTie();
   }

   @Override
   public void 
   prepareAgentCommentUserResponseForAMoveBy (int player) {

      updateWinOrTie();

      Comment currentAgentCommentAsComment;
      
      // null passed for scenarios here.
      if ( player == HUMAN_IDENTIFIER )
         currentAgentCommentAsComment = 
         commentingManager.getAgentCommentForHumanMove(
               gameState, latestHumanMove, null,
               gameState.getGameSpecificCommentingTags());
      else
         currentAgentCommentAsComment = 
         commentingManager.getAgentCommentForAgentMove(
               gameState, latestAgentMove, null,
               gameState.getGameSpecificCommentingTags());

      if ( currentAgentCommentAsComment == null )
         currentAgentComment = "";
      else
         currentAgentComment = 
         currentAgentCommentAsComment.getContent();

      currentHumanResponseOptions.clear();

      try{
         currentHumanResponseOptions.addAll(
               currentAgentCommentAsComment
               .getMultipleResponseOptions());
      }catch(Exception e){
         // in case no responses exists
      }
      
   }
   
   @Override
   public List<String> getCurrentHumanResponseOptions () {
      return CommentingManager.
            shuffleAndGetMax3(currentHumanResponseOptions);
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
         TTTUIListener listener) {
      this.listener = listener;
      nextStateTimer = new Timer();
      nextStateTimer.schedule(new NextStateTimerSetter(),
            NEXT_STATE_DELAY);
   }
   private class NextStateTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.nextState();
      }
   }
   
   private void updateWinOrTie () {

      winOrTie = gameState.didAnyOneJustWin();

      if ( winOrTie == 1 )
         gameState.userWins = true;
      else if ( winOrTie == 2 )
         gameState.agentWins = true;
      else if ( winOrTie == 3 )
         gameState.tie = true;

      if ( winOrTie != 0 )
         TTTClient.gameOver = true;
   }

   public void show () {
      proxy.startPlugin(PLUGIN_NAME, InstanceReuseMode.Reuse, null);
   }

   @Override
   public void startPluginForTheFirstTime (TTTUIListener listener) {
      updatePlugin(listener);
   }

   @Override
   public void updatePlugin (TTTUIListener listener) {
      this.listener = listener;
      show();
   }

   @Override
   public void makeBoardPlayable () {
      if ( gameState.didAnyOneJustWin() == 0 ) {
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
