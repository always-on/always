package wpi.edu.always.ttt;

import java.util.List;

import com.google.gson.JsonObject;

import edu.wpi.always.client.ClientPluginUtils;
import edu.wpi.always.client.Message;
import edu.wpi.always.client.MessageHandler;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.sgf.comment.*;
import edu.wpi.sgf.logic.*;
import edu.wpi.sgf.scenario.*;
import wpi.edu.always.ttt.sgf.logic.*;

public class TTTClient implements TTTUI {

   private static final String PLUGIN_NAME = "tictactoe";
   private static final String MSG_HUMAN_MOVE = "tictactoe.human_played_cell";
   private static final String MSG_AGENT_MOVE = "tictactoe.agent_cell";
   private static final String MSG_BOARD_PLAYABILITY = "tictactoe.playability";

   private static final int USER_IDENTIFIER = 1;
   private static final int AGENT_IDENTIFIER = 2;
   int winOrTie = 0; //1: userWins, 2: agentWins, 3: tie

   private String currentComment;
   private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;
   private TTTUIListener listener;

   private TTTLegalMoveGenerator moveGenerator;
   private TTTLegalMoveAnnotator moveAnnotator;
   private ScenarioManager scenarioManager;
   //private ScenarioFilter scenarioFilter;
   private MoveChooser moveChooser;
   private CommentingManager commentingManager;
   private TTTGameState gameState;

   public TTTClient (UIMessageDispatcher dispatcher){

      this.dispatcher = dispatcher;
      //startPlugin(dispatcher);
      moveGenerator = new TTTLegalMoveGenerator();
      moveAnnotator = new TTTLegalMoveAnnotator();
      commentingManager = new CommentingManager();
      //scenarioFilter = new ScenarioFilter();
      moveChooser = new MoveChooser();
      scenarioManager = new ScenarioManager();
      gameState = new TTTGameState();

      dispatcher.registerReceiveHandler(MSG_HUMAN_MOVE,
            new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            if ( listener != null ) {
               int cellNum = Integer.valueOf(body.get("cellNum").getAsString());
               gameState.board[cellNum - 1] = USER_IDENTIFIER;
               winOrTie = gameState.didAnyOneJustWin();
               if(winOrTie > 0)
                  makeBoardUnplayable();
               listener.humanPlayed();
            }
         }
      });

   }

   private void startPlugin(UIMessageDispatcher dispatcher) {
      Message m = Message.builder("start_plugin").add("name", "tictactoe").build();
      dispatcher.send(m);
   }

   @Override
   public void playAgentMove(TTTUIListener listener) {
      show(listener);
      scenarioManager.tickAll();
      gameState.board[((TTTLegalMove)currentMove.getMove()).getCellNum()] = AGENT_IDENTIFIER;
      Message msg = Message.builder(MSG_AGENT_MOVE)
            .add("cellNum", 
                  ((TTTLegalMove)currentMove.getMove()).getCellNum() + 1)
                  .build();
      dispatcher.send(msg);
      winOrTie = gameState.didAnyOneJustWin();
   }

   @Override
   public String getCurrentAgentComment() {
      return currentComment;
   }

   @Override
   public List<String> getCurrentHumanCommentOptions() {
      return commentingManager.getHumanCommentingOptions(gameState);
   }

   @Override
   public void prepareMoveAndComment() {

      currentComment = null;
      currentMove = null;

      currentMove = 
            //		moveChooser.choose(scenarioFilter.filter(
            //				moveAnnotator.annotate(moveGenerator.generate(
            //						gameState), gameState, 2), scenarioManager.getCurrentScenario()));
            moveChooser.choose(
                  moveAnnotator.annotate(moveGenerator.generate(
                        gameState), gameState, USER_IDENTIFIER));

      int winner = 
            gameState.didAnyOneJustWin();
      if(winner == 1)
         gameState.userWins = true;
      else if(winner == 2)
         gameState.agentWins = true;
      else if(winner == 3)
         gameState.tie = true;

      currentComment = 
            commentingManager.pickCommentOnOwnMove(
                  gameState, scenarioManager.getCurrentActiveScenarios(),
                  currentMove, AGENT_IDENTIFIER).getContent();

   }

   @Override
   public void gazeLeft() {

   }

   private void show(TTTUIListener listener) {
      this.listener = listener;
      ClientPluginUtils.startPlugin(dispatcher, PLUGIN_NAME,
            InstanceReuseMode.Reuse, null);
   }

   @Override
   public void startPluginForTheFirstTime(TTTUIListener listener) {
      this.listener = listener;
      startPlugin(dispatcher);
   }

   @Override
   public void updatePlugin(TTTUIListener listener) {
      show(listener);
   }

   @Override
   public void makeBoardPlayable() {
      if(gameState.didAnyOneJustWin() == 0){
         Message m = Message.builder(MSG_BOARD_PLAYABILITY).add("value", "true").build();
         dispatcher.send(m);
      }
   }

   @Override
   public void makeBoardUnplayable() {
      Message m = Message.builder(MSG_BOARD_PLAYABILITY).add("value", "false").build();
      dispatcher.send(m);      
   }

}
