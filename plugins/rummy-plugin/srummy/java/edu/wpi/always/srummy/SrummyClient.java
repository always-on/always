package edu.wpi.always.srummy;

import java.util.*;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.client.*;
import edu.wpi.always.srummy.game.*;
import edu.wpi.always.srummy.logic.*;
import edu.wpi.disco.rt.util.NullArgumentException;
import edu.wpi.sgf.comment.*;
import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.scenario.*;

public class SrummyClient implements SrummyUI {

   private static final String PLUGIN_NAME = "rummy";

   private static final String MSG_AVLBL_AGENT_MOVES = "rummy.available_moves"; //receives
   private static final String MSG_HUMAN_MOVE = "rummy.human_move"; //receives
   private static final String MSG_GAME_STATE = "rummy.game_state"; //receives
   private static final String MSG_PICKED_AGENT_MOVE = "rummy.agent_move"; //sends
   private static final String MSG_RESET_GAME = "rummy.reset_game"; //sends

   //   private final ConcurrentLinkedQueue<Message> inbox = 
   //         new ConcurrentLinkedQueue<Message>();

   private static final int HUMAN_COMMENTING_TIMEOUT = 15;
   private static final int AGENT_PLAY_DELAY_AMOUNT = 3;
   private static final int AGENT_PLAYING_GAZE_DELAY_AMOUNT = 2;

   public static String gazeDirection = "";
   public static boolean nod = false;

   public static boolean gameOver = false;

   private static final int HUMAN_IDENTIFIER = 1;
   //   private static final int AGENT_IDENTIFIER = 2;

   // 1: userWins, 2: agentWins, 3: tie
   private int winOrTie = 0;

   private String currentComment;
   //   private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;

   private final ClientProxy proxy;
   private SrummyUIListener listener;

   private Timer humanCommentingTimer;
   private Timer agentPlayDelayTimer;
   private Timer agentPlayingGazeDelayTimer;
   private Timer nextStateTimer;

   private SrummyLegalMoveFetcher moveFetcher;
   private SrummyLegalMoveAnnotator moveAnnotator;
   private ScenarioManager scenarioManager;
   //   private ScenarioFilter scenarioFilter;
   private MoveChooser moveChooser;
   private CommentingManager commentingManager;
   private SrummyGameState gameState;

   private SrummyAnnotatedLegalMove latestAgentMove;
   private SrummyAnnotatedLegalMove latestHumanMove;

   private List<SrummyLegalMove> possibleMoves;
   private List<AnnotatedLegalMove> annotatedMoves;
   private List<AnnotatedLegalMove> passedMoves;
   private List<SrummyLegalMove> humanPlayedMoves;
   private int hashCodeOfTheSelectedMove;
   private int numOfPossibleDiscards
   , numOfPossibleMelds, numOfPossibleLayoffs;

   public SrummyClient (ClientProxy proxy, UIMessageDispatcher dispatcher) {

      this.proxy = proxy;
      this.dispatcher = dispatcher;

      registerHandlerFor(MSG_GAME_STATE);
      registerHandlerFor(MSG_HUMAN_MOVE);
      registerHandlerFor(MSG_AVLBL_AGENT_MOVES);

      //      startPlugin(dispatcher);
      moveFetcher = new SrummyLegalMoveFetcher();
      moveAnnotator = new SrummyLegalMoveAnnotator();
      commentingManager = new SrummyCommentingManager();
      //      scenarioFilter = new ScenarioFilter();
      gameState = new SrummyGameState();
      possibleMoves = new ArrayList<SrummyLegalMove>();
      annotatedMoves = new ArrayList<AnnotatedLegalMove>();
      passedMoves = new ArrayList<AnnotatedLegalMove>();
      scenarioManager = new ScenarioManager();
      moveChooser = new MoveChooser();
      humanPlayedMoves = new ArrayList<SrummyLegalMove>();
      hashCodeOfTheSelectedMove = 0;
      //      scenarioManager.chooseOrUpdateScenario();

      //      dispatcher.registerReceiveHandler(MSG_HUMAN_MOVE, new MessageHandler() {
      //         @Override
      //         public void handleMessage (JsonObject body) {
      //            if ( listener != null ) {
      //               int cellNum = Integer.valueOf(body.get("cellNum").getAsString());
      //               gameState.updateLastBoardState();
      //               gameState.board[cellNum - 1] = HUMAN_IDENTIFIER;
      //               latestHumanMove = (SrummyLegalMove) moveAnnotator
      //                     .annotate(new SrummyLegalMove(cellNum - 1), gameState);
      //               updateWinOrTie();
      //               if ( winOrTie > 0 )
      //                  makeBoardUnplayable();
      //               listener.humanPlayed();
      //            }
      //         }
      //      });

   }

   private void registerHandlerFor (String messageType) {
      final String t = messageType;
      dispatcher.registerReceiveHandler(t, new MessageHandler() {
         @Override
         public void handleMessage (JsonObject body) {
            receivedMessage(new Message(t, body));
         }
      });
   }

   private void receivedMessage (Message message) {

      if(message.getType()
            .equals(MSG_AVLBL_AGENT_MOVES)){
         extractPossibleMoves(message);
         getTheHashCodeAmongAllAvailableMovesFor(
               chooseOneAmongAgentAvailableMoves());

         listener.receivedAgentMoveOptions();
         updateWinOrTie();
         //extracts options, choose one move, saves its hash code

         //tickAll
         //      scenarioManager.tickAll();

         //make the comment on own move, based on annotations and scenario
         //         Comment cm = null;
         //         cm = commentingManager
         //               .pickCommentOnOwnMove(selectedMove);
         //         if(cm != null)
         //            return cm.getContent();
         //
         //         return "";
      }
      if(message.getType()
            .equals(MSG_HUMAN_MOVE)){
         try {
            extractHumanMove(message);
         } catch (Exception e) {
            System.out.println("Error in "
                  + "processing human move.");
            e.printStackTrace();
         }
         listener.humanPlayed();
         updateWinOrTie();
      }
      if(message.getType()
            .equals(MSG_GAME_STATE)){
         try {
            gameState.synchGame(message);
         } catch (Exception e) {
            System.out.println("Error in "
                  + "synchronizing game state.");
            e.printStackTrace();
         }
         listener.receivedNewState();
      }

   }

   @Override
   public String getCurrentAgentComment () {
      return currentComment;
   }

   @Override
   public List<String> getCurrentHumanCommentOptionsForAMoveBy (int player) {

      updateWinOrTie();

      if ( player == HUMAN_IDENTIFIER )
         return commentingManager.getHumanCommentingOptionsForHumanMove(
               gameState, latestHumanMove,
               gameState.getGameSpecificCommentingTags());
      else
         return commentingManager.getHumanCommentingOptionsForAgentMove(
               gameState, latestAgentMove,
               gameState.getGameSpecificCommentingTags());
   }

   //   public void fetchAgentMoveOptions () {
   //      currentMove =
   //            // moveChooser.choose(scenarioFilter.filter(
   //            // moveAnnotator.annotate(moveGenerator.generate(
   //            // gameState), gameState), scenarioManager.getCurrentScenario()));
   //            moveChooser.choose(moveAnnotator.annotate(
   //                  moveFetcher.getCurrentPossibleMoves(), gameState));
   //      updateWinOrTie();
   //   }

   @Override
   public void prepareAgentCommentForAMoveBy (int player) {

      updateWinOrTie();

      // null passed for scenarios here.
      if ( player == HUMAN_IDENTIFIER )
         currentComment = commentingManager.getAgentCommentForHumanMove(
               gameState, latestHumanMove, null,
               gameState.getGameSpecificCommentingTags());
      else
         currentComment = commentingManager.getAgentCommentForAgentMove(
               gameState, latestAgentMove, null,
               gameState.getGameSpecificCommentingTags());

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
   public void triggerNextStateTimer () {
      nextStateTimer = new Timer();
      nextStateTimer.schedule(new testTimerSetter(),
            4000);
   }
   private class testTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.nextState();
      }
   }

   /**Finding the chosen move in the initial list to send its hashCode back
    * 0 is sent if draw move was the option. Later for cheating the hash-code of
    * the chosen card.
    */
   private void getTheHashCodeAmongAllAvailableMovesFor(
         AnnotatedLegalMove selectedMove){

      hashCodeOfTheSelectedMove = 0;
      for(SrummyLegalMove eachMove : possibleMoves)
         if(eachMove.equals(selectedMove.getMove()))
            hashCodeOfTheSelectedMove = eachMove.hashCode();
      if(hashCodeOfTheSelectedMove == 0){
         try {throw new Exception(
               "Chosen Move apparantly not in the initial Moves!");
         } catch (Exception e) {e.printStackTrace();}
      }

      latestAgentMove = 
            (SrummyAnnotatedLegalMove) selectedMove;

      //      //debugging log
      //      System.out.println("\n\n******HASH\n\n+ " +
      //            hashCodeOfTheSelectedMove + 
      //            "\n\n******HASH");

      //       System.out.println("*******************");
      //       System.out.println(moveAnnotator.toString(a));
      //       System.out.println("*******************");
      //
      //       System.out.println("\n***************************\n");
      //       for(Move eachMove : possibleMoves)
      //          System.out.println(eachMove.toString() + "\n");
      //       System.out.println("\n***************************\n");

      //    if(msg.getType().equals(MSG_HUMAN_MOVE)){
      //
      //    }

   }

   /**Fetch moves, annotate, filter by scenarios.
      note: moveFetcher will keep an up-to-date copy of the current move
      options, could also be used for possible use of other parts of the 
      framework.
    */
   private AnnotatedLegalMove chooseOneAmongAgentAvailableMoves () {

      moveFetcher.fetch(possibleMoves);
      annotatedMoves.addAll(moveAnnotator
            .annotate(moveFetcher.getCurrentPossibleMoves(), gameState));
      passedMoves.addAll(
            //scenarioFilter.filter(
            annotatedMoves 
            //, scenarioManager.getCurrentScenario())
            );
      return moveChooser.choose(passedMoves);

   }

   private void extractPossibleMoves (Message msg) {

      numOfPossibleDiscards = 
            numOfPossibleMelds = 
            numOfPossibleLayoffs = 0;

      possibleMoves.clear();
      annotatedMoves.clear();
      passedMoves.clear();

      JsonObject allPossibleMovesJsonObj = 
            msg.getBody();

      while(allPossibleMovesJsonObj
            .has("discard"+(++numOfPossibleDiscards))
            && allPossibleMovesJsonObj != null){

         String aDiscardMoveDescription = allPossibleMovesJsonObj
               .getAsJsonObject("discard"+(numOfPossibleDiscards))
               .get("card").getAsString();

         possibleMoves.add(new DiscardMove(
               Player.Agent, new Card(
                     aDiscardMoveDescription)));

      }

      while(allPossibleMovesJsonObj
            .has("meld"+(++numOfPossibleMelds))
            && allPossibleMovesJsonObj != null){

         String meldCardsDescriptionsString = allPossibleMovesJsonObj
               .getAsJsonObject("meld"+(numOfPossibleMelds))
               .get("meldcards").getAsString();

         List<String> meldCardsDescriptionsList = 
               new ArrayList<String>(Arrays.asList(
                     meldCardsDescriptionsString.split("/")));

         List<Card> meldCards = new ArrayList<Card>();
         for(String eachCardDescription : meldCardsDescriptionsList)
            meldCards.add(new Card(eachCardDescription));

         possibleMoves.add(new MeldMove(
               Player.Agent, new Meld(meldCards)));

      }

      while(allPossibleMovesJsonObj
            .has("layoff"+(++numOfPossibleLayoffs))
            && allPossibleMovesJsonObj != null){

         String layoffMoveCardDescription = allPossibleMovesJsonObj
               .getAsJsonObject("layoff"+(numOfPossibleLayoffs))
               .get("card").getAsString();

         String layoffMoveMeldCards = allPossibleMovesJsonObj
               .getAsJsonObject("layoff"+(numOfPossibleLayoffs))
               .get("meldcards").getAsString();

         Card layOffCard = 
               new Card(layoffMoveCardDescription);

         List<String> meldCardsDescriptionsList = 
               new ArrayList<String>(Arrays.asList(
                     layoffMoveMeldCards.split("/")));

         List<Card> meldCards = new ArrayList<Card>();
         for(String eachCardDescription : meldCardsDescriptionsList)
            meldCards.add(new Card(eachCardDescription));

         possibleMoves.add(new LayoffMove(
               Player.Agent, layOffCard, new Meld(meldCards)));

      }

      if(allPossibleMovesJsonObj.has("draw") 
            && allPossibleMovesJsonObj != null){

         //if later cheating
         //possibleMoves.add(new DrawMove(Player.Agent, Which PILE?));

      }
   }

   private void extractHumanMove(
         Message humanMoveAsMessage) {

      JsonObject humanMoveAsJsonObj = 
            humanMoveAsMessage.getBody();

      if(humanMoveAsJsonObj == null)
         throw new NullArgumentException(
               "Human Move not received as null.");

      if(humanMoveAsJsonObj.has("layoff")){

         String layoffMoveCardDescription = humanMoveAsJsonObj
               .getAsJsonObject("layoff").get("card").getAsString();
         String layoffMoveMeldCards = humanMoveAsJsonObj
               .getAsJsonObject("layoff")
               .get("meldcards").getAsString();

         Card layOffCard = 
               new Card(layoffMoveCardDescription);

         List<String> meldCardsDescriptionsList = 
               new ArrayList<String>(Arrays.asList(
                     layoffMoveMeldCards.split("/")));

         List<Card> meldCards = new ArrayList<Card>();
         for(String eachCardDescription : meldCardsDescriptionsList)
            meldCards.add(new Card(eachCardDescription));

         humanPlayedMoves.add(new LayoffMove(
               Player.Human, layOffCard, new Meld(meldCards)));
         //could also 'happen()' human move here, 
         //but it is also affected the transmitted game state.
      }
      else if(humanMoveAsJsonObj.has("meld")){

         String meldCardsDescriptionsString = humanMoveAsJsonObj
               .getAsJsonObject("meld")
               .get("meldcards").getAsString();

         List<String> meldCardsDescriptionsList = 
               new ArrayList<String>(Arrays.asList(
                     meldCardsDescriptionsString.split("/")));

         List<Card> meldCards = new ArrayList<Card>();
         for(String eachCardDescription : meldCardsDescriptionsList)
            meldCards.add(new Card(eachCardDescription));

         humanPlayedMoves.add(new MeldMove(
               Player.Human, new Meld(meldCards)));
         
      }
      else if(humanMoveAsJsonObj.has("discard")){

         String aDiscardMoveDescription = humanMoveAsJsonObj
               .getAsJsonObject("discard")
               .get("card").getAsString();

         humanPlayedMoves.add(new DiscardMove(
               Player.Human, new Card(
                     aDiscardMoveDescription)));
      }
      
      //<<if cheating, draw should be added<<
      //not there now, as does not contain any sgf value
      else 
         return;
      
      latestHumanMove = (SrummyAnnotatedLegalMove)
            moveAnnotator.annotate(humanPlayedMoves
            .get(humanPlayedMoves.size() - 1), gameState);
      
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
         SrummyClient.gameOver = true;
   }

   public void show () {
      proxy.startPlugin(PLUGIN_NAME, InstanceReuseMode.Reuse, null);
   }

   @Override
   public void startPluginForTheFirstTime (SrummyUIListener listener) {
      //different method for doing extra things if necessary, 
      //remove later if none.
      updatePlugin(listener);
   }

   @Override
   public void updatePlugin (SrummyUIListener listener) {
      this.listener = listener;
      show();
   }

   @Override
   public void sendBackAgentMove () {
      Message m = Message.builder(MSG_PICKED_AGENT_MOVE)
            .add("hashcode", hashCodeOfTheSelectedMove).build();
      dispatcher.send(m);
      //updateWinOrTie();
   }

   @Override
   public void resetGame () {
      Message m = Message.builder(MSG_RESET_GAME)
            .add("value", "reset").build();
      dispatcher.send(m);
   }

   //   @Override
   //   public void makeBoardPlayable () {
   //      if ( gameState.didAnyOneJustWin() == 0 ) {
   //         Message m = Message.builder(MSG_BOARD_PLAYABILITY)
   //               .add("value", "true").build();
   //         dispatcher.send(m);
   //      }
   //   }

   //   @Override
   //   public void makeBoardUnplayable () {
   //      Message m = Message.builder(MSG_BOARD_PLAYABILITY).add("value", "false")
   //            .build();
   //      dispatcher.send(m);
   //   }

}
