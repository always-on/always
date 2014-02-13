package edu.wpi.always.checkers;

import java.util.*;
import com.google.gson.JsonObject;
import edu.wpi.always.checkers.logic.*;
import edu.wpi.always.client.*;
import edu.wpi.sgf.scenario.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.sgf.comment.Comment;
import edu.wpi.sgf.comment.CommentLibraryHandler;
import edu.wpi.sgf.comment.CommentingManager;
import edu.wpi.sgf.logic.AnnotatedLegalMove;

public class CheckersClient implements CheckersUI {

   private static final String PLUGIN_NAME = "checkers";
   private static final String MSG_AGENT_MOVE = "checkers.agent_move";
   private static final String MSG_HUMAN_MOVE = "checkers.human_played_move";
   private static final String MSG_CONFIRM_HUMAN_MOVE = "checkers.confirm_human_move";
   private static final String MSG_HUMAN_TOUCHED_AGENT_PIECE = "checkers.touched_agent_piece";
   private static final String MSG_BOARD_PLAYABILITY = "checkers.playability";
   private static final String MSG_RESET = "checkers.reset";
   public static List<String> shouldHaveJumpedClarificationStringOptions =
         new ArrayList<String>();
   public static List<String> shouldJumpAgainClarificationStringOptions =
         new ArrayList<String>();
   public static List<String> humantouchedAgentCheckerClarificationStringOptions =
         new ArrayList<String>();
   public static List<String> humantouchedTooMuchClarificationStringOptions =
         new ArrayList<String>();

   private static final int HUMAN_COMMENTING_TIMEOUT = 30;
   private static final double AGENT_PLAY_DELAY_AMOUNT = 5.5;
   private static final int AGENT_PLAYING_GAZE_DELAY_AMOUNT = 3;
   private static final int NEXT_STATE_DELAY = 3000;//ms

   public static String gazeDirection = "";
   public static boolean userJumpedAtLeastOnceInThisTurn = false;
   public static boolean moreJumpsPossible = false;
   
   public static boolean nod = false;
   public static boolean gameOver = false;
   private static final int HUMAN_IDENTIFIER = 1;
   
   public static boolean thereAreGameSpecificTags = false;

   private String currentAgentComment;
   private List<String> currentHumanResponseOptions;
   private Map<String, String> currentAgentResponseOptions;
   private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;
   private final ClientProxy proxy;
   private CheckersUIListener listener;

   private CheckersLegalMoveGenerator moveGenerator;
   private CheckersLegalMoveAnnotator moveAnnotator;
   private ScenarioManager scenarioManager;
   // private ScenarioFilter scenarioFilter;
   private CheckersMoveChooser moveChooser;
   private CommentingManager commentingManager;
   private CheckersGameState gameState;

   private Timer humanCommentingTimer;
   private Timer agentPlayDelayTimer;
   private Timer agentPlayingGazeDelayTimer;
   private Timer nextStateTimer;
   private Timer agentMultiJumpTimer;

   private AnnotatedLegalMove latestAgentMove;
   private AnnotatedLegalMove latestHumanMove;
   
   private static boolean agentMultiJumpInProcess;
   
   public static Random random;

   public CheckersClient (ClientProxy proxy, UIMessageDispatcher dispatcher) {
      this.proxy = proxy;
      this.dispatcher = dispatcher;
      //startPlugin(dispatcher); 
      
      /* >> initializing special situation agent prompt feedback
      options. These are NOT framework comments. */
      //1. user does not jump when she/he can
      shouldHaveJumpedClarificationStringOptions.
      add("In checkers, if you can jump, you have to jump");
      shouldHaveJumpedClarificationStringOptions.
      add("You should jump whenever you can");
      shouldHaveJumpedClarificationStringOptions.
      add("You can jump, do it");
      //2. user does not "continue" to jump when she/he can
      shouldJumpAgainClarificationStringOptions
      .add("You can jump once more!");
      shouldJumpAgainClarificationStringOptions
      .add("Look, You can jump again!");
      shouldJumpAgainClarificationStringOptions
      .add("You can jump all the way!");
      //3. user touches agent stuff!
      humantouchedAgentCheckerClarificationStringOptions
      .add("wait that is mine!");
      humantouchedAgentCheckerClarificationStringOptions
      .add("No. You cannot move mine!");
      humantouchedAgentCheckerClarificationStringOptions
      .add("Oh no cheating!");
      humantouchedAgentCheckerClarificationStringOptions
      .add("Let's do a fair play, shall we?!");
      humantouchedAgentCheckerClarificationStringOptions
      .add("Not my checkers!");
      //4. user touches agent stuff too much!!
      humantouchedTooMuchClarificationStringOptions
      .add("really?!");
      humantouchedTooMuchClarificationStringOptions
      .add("Seriously?!");
      humantouchedTooMuchClarificationStringOptions
      .add("Oh, come on!");
      // <<
      
      moveGenerator = new CheckersLegalMoveGenerator();
      moveAnnotator = new CheckersLegalMoveAnnotator();
      commentingManager = new CheckersCommentingManager();
      //scenarioFilter = new ScenarioFilter();
      moveChooser = new CheckersMoveChooser();
      scenarioManager = new ScenarioManager();
      gameState = new CheckersGameState();
      currentHumanResponseOptions = new ArrayList<String>();
      currentAgentResponseOptions = new HashMap<String, String>();
      
      random = new Random();
      random.setSeed(12345);
      
      dispatcher.registerReceiveHandler(MSG_HUMAN_MOVE, new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            
            if ( listener != null ) {
               String moveDesc = body.get("humanMove").getAsString();
               
               CheckersLegalMove humanMove = new CheckersLegalMove(
                     Integer.parseInt(moveDesc.split("//")[0].split(",")[0]), 
                     Integer.parseInt(moveDesc.split("//")[0].split(",")[1]), 
                     Integer.parseInt(moveDesc.split("//")[1].split(",")[0]), 
                     Integer.parseInt(moveDesc.split("//")[1].split(",")[1]));
               
               //2 if user could have jumped but did not and
               //1 if user can jump more
               int stat = gameState.checkAndPlayHumanMove(humanMove);
               if(stat == 2)
                  listener.shouldHaveJumped();
               else if(stat == 1){
                  confirmHumanMove();
                  latestHumanMove = moveAnnotator
                        .annotate(humanMove, gameState);
                  listener.shouldHaveJumped();
               }
               else if(stat == 0){
                  confirmHumanMove();
                  latestHumanMove = moveAnnotator
                        .annotate(humanMove, gameState);
                  updateWin();
                  if (gameState.possibleWinner() > 0)
                     makeBoardUnplayable();
                  listener.receivedHumanMove();
               }
            }
         }
      });
      
      dispatcher.registerReceiveHandler(
            MSG_HUMAN_TOUCHED_AGENT_PIECE, new MessageHandler() {
               @Override
               public void handleMessage (JsonObject body) {
                  if ( listener != null ) {
                     listener.humanTouchedAgentStuff(
                           body.get("howMany").getAsInt());
                  }
               }
            });
   }

   /** Sends back confirmation that user move was 
    * valid. Otherwise, agent says something.*/
   private void confirmHumanMove () {
      
      Message msg = Message
            .builder(MSG_CONFIRM_HUMAN_MOVE)
            .add("human_move",
                  ("confirmed"))
            .build();
      
      dispatcher.send(msg);
      
   }
   
   @Override
   public void processAgentMove (CheckersUIListener listener) {

      this.listener = listener;
      show();
      if ( currentMove == null )
         return;
      scenarioManager.tickAll();
      latestAgentMove = currentMove;
      moreJumpsPossible = gameState.playAgentMove(
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
   public void triggerAgentMultiJumpTimer (CheckersUIListener listener) {
      this.listener = listener;
      moreJumpsPossible = false;
      agentMultiJumpTimer = new Timer();
      agentMultiJumpTimer.schedule(new AgentMultiJumpTimerAction(),
            2000);
   }
   
   class AgentMultiJumpTimerAction extends TimerTask {
      @Override
      public void run () {
         agentMultiJumpInProcess = true;
         prepareAgentMove();
         latestAgentMove = currentMove;
         moreJumpsPossible = gameState.playAgentMove(
               (CheckersLegalMove)currentMove.getMove());
         Message msg = Message
               .builder(MSG_AGENT_MOVE)
               .add("moveDesc",
                     (gameState.makeMoveDesc(
                           (CheckersLegalMove) currentMove.getMove())))
                           .build();
         dispatcher.send(msg);
         updateWin();
         listener.agentMultiJumpedOneMore();
      }
   }

   @Override
   public void resetGame () {
      Message msg = Message.builder(MSG_RESET).add("command", "reset")
            .build();
      gameState.resetGame();
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
      
      updateWin();

      List<Comment> humanCommentingOptions = 
            new ArrayList<Comment>();
      
      if ( player == HUMAN_IDENTIFIER )
         humanCommentingOptions.addAll(
               commentingManager.getHumanCommentingOptionsAndAnAgentResponseForHumanMove(
               gameState, latestHumanMove,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestHumanMove.getMove(), player)));
      else
         humanCommentingOptions.addAll(
               commentingManager.getHumanCommentingOptionsForAgentMove(
               gameState, latestAgentMove,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestAgentMove.getMove(), player)));
      
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
            moveGenerator.generate(gameState), gameState), agentMultiJumpInProcess);
      agentMultiJumpInProcess = false;
      updateWin();
   }

   @Override
   public void prepareAgentCommentUserResponseForAMoveBy (int player) {

      updateWin();
      
      Comment currentAgentCommentAsComment;

      // null passed for scenarios here.
      if ( player == HUMAN_IDENTIFIER )
         currentAgentCommentAsComment =  
         commentingManager.getAgentCommentForHumanMove(
               gameState, latestHumanMove, null,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestHumanMove.getMove(), player));
      else
         currentAgentCommentAsComment = 
         commentingManager.getAgentCommentForAgentMove(
               gameState, latestAgentMove, null,
               gameState.getGameSpecificCommentingTags(
                     (CheckersLegalMove)latestAgentMove.getMove(), player));
     
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
            ((int)(1000 * AGENT_PLAY_DELAY_AMOUNT)));
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
   public void triggerNextStateTimer (CheckersUIListener listener) {
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
   
   private void updateWin () {
      int res = gameState.possibleWinner();
      if ( res != 0 ){
         CheckersClient.gameOver = true;
         if( res == 1 )
            gameState.userWins = true;
         else
            gameState.agentWins = true;
      }
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
