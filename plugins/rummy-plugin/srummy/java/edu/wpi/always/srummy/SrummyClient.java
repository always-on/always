package edu.wpi.always.srummy;

import java.util.*;
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
   private static final String MSG_GAME_OVER = "rummy.gameover"; //sends
   private static final String MSG_BOARD_PLAYABILITY = "rummy.playability";//sends
   private static final String MSG_SETUP_BOARD = "rummy.setupgame";//sends
   private static final String MSG_STARTING_PLAYER = "rummy.starting_player";//sends
   private static final String MSG_RESET_GAME = "rummy.reset";//sends

   private static final int HUMAN_COMMENTING_TIMEOUT = 15;//not currently used
   private static final int AGENT_PLAY_DELAY_AMOUNT = 6;
   private static final int AGENT_PLAYING_GAZE_DELAY_AMOUNT = 3;
   private static final int AGENT_DRAWING_DISCARDING_DELAY = 1;

   public static int gameRound = 0;
   public static String gazeDirection = "";
   public static boolean limboEnteredOnce = false;
   public static boolean nod = false;
   public static boolean gameOver = false;
   public static boolean DelayAfterDraw = false;
   public static boolean meldedOnce = false;
   public static boolean agentDrew = false;
   public static boolean twoMeldsInARowByAgent = false;
   public static boolean twoMeldsInARowByHuman = false;
   public static boolean oneMeldInHumanTurnAlready = false;
   public static boolean oneLayoffInHumanTurnAlready = false;
   public static boolean oneMeldInAgentTurnAlready = false;
   public static boolean oneLayoffInAgentTurnAlready = false;
   
   public static boolean thereAreGameSpecificTags = false;

   private static final int HUMAN_IDENTIFIER = 1;
   //private static final int AGENT_IDENTIFIER = 2;

   // 1: userWins, 2: agentWins, 3: tie
   private int winOrTie = 0;

   private String currentAgentComment;
   private Map<String, String> currentAgentResponseOptions;
   private List<String> currentHumanResponseOptions;
   //private AnnotatedLegalMove currentMove;

   private final UIMessageDispatcher dispatcher;

   private final ClientProxy proxy;
   private SrummyUIListener listener;

   private Timer humanCommentingTimer;
   private Timer agentPlayDelayTimer;
   private Timer agentPlayingGazeDelayTimer;
   private Timer agentDiscardOrMeldLayoffDelayTimer;
   private Timer nextStateTimer;
   private Timer waitMoreForDrawOptionsTimer;
   private Timer waitMoreForDiscardMeldLayoffOptionsTimer;

   private SrummyLegalMoveFetcher moveFetcher;
   private SrummyLegalMoveAnnotator moveAnnotator;
   @SuppressWarnings("unused")//temp
   private ScenarioManager scenarioManager;
   @SuppressWarnings("unused")//temp
   private ScenarioFilter scenarioFilter;
   //   private ScenarioFilter scenarioFilter;
   private MoveChooser moveChooser;
   private CommentingManager commentingManager;
   private SrummyGameState gameState;

   private static AnnotatedLegalMove latestAgentMove;
   private static AnnotatedLegalMove latestHumanMove;
//   private Card latestAgentDrawnCard;

   private List<SrummyLegalMove> possibleMoves;
   private List<AnnotatedLegalMove> annotatedMoves;
   private List<AnnotatedLegalMove> passedMoves;
   private List<SrummyLegalMove> humanPlayedMoves;
   private int hashCodeOfTheSelectedMove;
   private int numOfPossibleDiscards, numOfPossibleMelds
   , numOfPossibleLayoffs, numOfPossibleDraws;
   
   public static Random random;

   public SrummyClient (ClientProxy proxy, UIMessageDispatcher dispatcher) {

      this.proxy = proxy;
      this.dispatcher = dispatcher;

      registerHandlerFor(MSG_GAME_STATE);
      registerHandlerFor(MSG_HUMAN_MOVE);
      registerHandlerFor(MSG_AVLBL_AGENT_MOVES);
      registerHandlerFor(MSG_GAME_OVER);

      moveFetcher = new SrummyLegalMoveFetcher();
      moveAnnotator = new SrummyLegalMoveAnnotator();
      commentingManager = new SrummyCommentingManager();
      scenarioFilter = new ScenarioFilter();
      gameState = new SrummyGameState();
      possibleMoves = new ArrayList<SrummyLegalMove>();
      annotatedMoves = new ArrayList<AnnotatedLegalMove>();
      passedMoves = new ArrayList<AnnotatedLegalMove>();
      scenarioManager = new ScenarioManager();
      moveChooser = new MoveChooser();
      humanPlayedMoves = new ArrayList<SrummyLegalMove>();
      hashCodeOfTheSelectedMove = 0;
      currentHumanResponseOptions = new ArrayList<String>();
      currentAgentResponseOptions = new HashMap<String, String>();
      //scenarioManager.chooseOrUpdateScenario();
      
      random = new Random();
      random.setSeed(12345);

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
   
   @Override
   public void setUpGame () {
      Message m = Message.builder(MSG_SETUP_BOARD)
            .build();
      dispatcher.send(m);
   }
   
   @Override
   public void setStartingPlayer (int playerIdentifier) {
      String who = playerIdentifier == HUMAN_IDENTIFIER ? 
         "human" : "agent";
      Message m = Message.builder(MSG_STARTING_PLAYER)
            .add("who", who).build();
      dispatcher.send(m);
      
   }

   private void receivedMessage (Message message) {
      
      if(message.getType()
            .equals(MSG_GAME_OVER)){
         String winner = message.getBody().get("winner")
         .getAsString().toLowerCase().trim();
         gameState.gameOver(winner);
         SrummyClient.gameOver = true;
         listener.gameIsOverByYieldingZeroCardsInATurn();
      }
      
      if(message.getType()
            .equals(MSG_AVLBL_AGENT_MOVES)){
         //extracts options, choose one, saves its hash
         extractPossibleMoves(message);
         getTheHashCodeAmongAllAvailableMovesFor(
               chooseOneAmongAgentAvailableMoves());

         String chosenMoveType = "";
         if(latestAgentMove.getMove() instanceof DrawMove)
            chosenMoveType = "draw";
         else if(latestAgentMove.getMove() instanceof MeldMove){
            chosenMoveType = "meld";
            oneMeldInAgentTurnAlready = true;
         }
         else if(latestAgentMove.getMove() instanceof LayoffMove){
            chosenMoveType = "layoff";
            oneLayoffInAgentTurnAlready = true;
         }
         else if(latestAgentMove.getMove() instanceof DiscardMove)
            chosenMoveType = "discard";

         listener.receivedAgentMoveOptions(chosenMoveType);

         updateWinOrTie();
         //tickAll
         //scenarioManager.tickAll();
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

         //>> the logic below checks if two melds is done by human in a turn
         boolean isMeld = latestHumanMove.getMove() instanceof MeldMove;
         if(isMeld && oneMeldInHumanTurnAlready)
            twoMeldsInARowByHuman = true;
         if(isMeld)
            oneMeldInHumanTurnAlready = true;

         if(latestHumanMove.getMove() instanceof LayoffMove)
            oneLayoffInHumanTurnAlready = true;

         /* flags ABOVE get reset in StartGamingSequence#AgentPlayDelay#enter()
         since once there, user has not played again yet, 
         and comments on user move is concluded */

         //note: Human draw is not sent from GUI, 
         //as contains no strategy/commenting value. 
         //if meld or lay-off is sent in between, 
         //waits for the discard which "concludes" human's turn.
         if(!(latestHumanMove.getMove() instanceof MeldMove)
               && !(latestHumanMove.getMove() instanceof LayoffMove)){
            listener.receivedHumanMove();
            SrummyClient.gameRound += 1;
            SrummyClient.oneMeldInAgentTurnAlready = false;
            SrummyClient.oneLayoffInAgentTurnAlready = false;
         }
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
   public void prepareAgentCommentUserResponseForAMoveBy (int player) {

      updateWinOrTie();

      Comment currentAgentCommentAsComment;
      
      // null passed for scenarios here
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
   public void triggerAgentPlayTimers () {
      agentPlayDelayTimer = new Timer();
      agentPlayingGazeDelayTimer = new Timer();
      agentDiscardOrMeldLayoffDelayTimer = new Timer();
      agentPlayDelayTimer.schedule(
            new AgentPlayDelayTimerSetter(),
            1000 * AGENT_PLAY_DELAY_AMOUNT);
      agentPlayingGazeDelayTimer.schedule(
            new AgentPlayingGazeDelayTimerSetter(),
            1000 * AGENT_PLAYING_GAZE_DELAY_AMOUNT);
      agentDiscardOrMeldLayoffDelayTimer.schedule(
            new AgentDrawDelayTimerSetter(), 
            1000 * AGENT_DRAWING_DISCARDING_DELAY);
   }
   
   @Override
   public void cancelUpcomingTimersTillNextRound (SrummyUIListener listener) {
      this.listener = listener;
      agentPlayDelayTimer.cancel();
      agentPlayDelayTimer.purge();
      agentPlayingGazeDelayTimer.cancel();
      agentPlayingGazeDelayTimer.purge();
   }
   
   @Override
   public void waitMoreForAgentDrawOptions 
   (SrummyUIListener listener) {
      this.listener = listener;
      waitMoreForDrawOptionsTimer = new Timer();
      waitMoreForDrawOptionsTimer
      .schedule(new RobustDrawOptionsRetrieval(), 1000);
      
   }
   private class RobustDrawOptionsRetrieval extends TimerTask {
      @Override
      public void run () {
         listener.waitingForAgentDrawOptionsOver();
      }
   }
   
   @Override
   public void waitMoreForAgentDiscardMeldLayoff 
   (SrummyUIListener listener) {
      this.listener = listener;
      waitMoreForDiscardMeldLayoffOptionsTimer = new Timer();
      waitMoreForDiscardMeldLayoffOptionsTimer
      .schedule(new RobustDiscardMeldLayoffOptionsRetrieval(), 1000);
   }
   private class RobustDiscardMeldLayoffOptionsRetrieval extends TimerTask {
      @Override
      public void run () {
         //can use this here too
         listener.agentPlayDelayOver();
      }
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
   private class AgentDrawDelayTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.agentDrawDelayOver();
      }
   }
   
   @Override
   public void triggerAgentDiscardOrMeldLayoffDelay () {
      agentDiscardOrMeldLayoffDelayTimer = new Timer();
      agentDiscardOrMeldLayoffDelayTimer.schedule(
            new AgentDiscardDelayTimerSetter(), 
            1000 * AGENT_DRAWING_DISCARDING_DELAY);
   }
   private class AgentDiscardDelayTimerSetter extends TimerTask {
      @Override
      public void run () {
         listener.agentDiscardDelayOver();
      }
   }

   @Override
   public void triggerNextStateTimer (SrummyUIListener listener) {
      this.listener = listener;
      nextStateTimer = new Timer();
      nextStateTimer.schedule(new NextStateTimerSetter(),
            3000);
   }
   private class NextStateTimerSetter extends TimerTask {
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

      latestAgentMove = selectedMove;

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

      AnnotatedLegalMove passedMove = 
            moveChooser.choose(passedMoves);
      
      if(passedMove.getMove() instanceof DrawMove)
         agentDrew = true;
      
      if(passedMove.getMove() instanceof MeldMove)
         meldedOnce = true;
      
      return passedMove;
   }

   private void extractPossibleMoves (Message msg) {

      numOfPossibleDiscards = 
            numOfPossibleMelds = 
            numOfPossibleLayoffs =
            numOfPossibleDraws = 0;

      possibleMoves.clear();
      annotatedMoves.clear();
      passedMoves.clear();

      JsonObject allPossibleMovesJsonObj = 
            msg.getBody();

      while(allPossibleMovesJsonObj
            .has("discard"+(++numOfPossibleDiscards))){

         String aDiscardMoveDescription = allPossibleMovesJsonObj
               .getAsJsonObject("discard"+(numOfPossibleDiscards))
               .get("card").getAsString();

         possibleMoves.add(new DiscardMove(
               Player.Agent, new Card(
                     aDiscardMoveDescription)));

      }

      while(allPossibleMovesJsonObj
            .has("meld"+(++numOfPossibleMelds))){

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
            .has("layoff"+(++numOfPossibleLayoffs))){

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

      while(allPossibleMovesJsonObj
            .has("draw"+(++numOfPossibleDraws))){

         String aDrawMovePileName = allPossibleMovesJsonObj
               .getAsJsonObject("draw"+(numOfPossibleDraws))
               .get("pile").getAsString();

         Pile aPile;
         if(aDrawMovePileName.trim()
               .toLowerCase().contains("stock"))
            aPile = Pile.Stock;
         else
            aPile = Pile.Discard;

         possibleMoves.add(new DrawMove(
               Player.Agent, aPile));

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

      latestHumanMove = moveAnnotator.annotate(humanPlayedMoves.get(
            humanPlayedMoves.size() - 1), gameState).get(0);

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
   
   public static AnnotatedLegalMove getLatestAgentMove(){return latestAgentMove;}
   public static AnnotatedLegalMove getLatestHumanMove(){return latestHumanMove;}
   
   public void show () {
      proxy.startPlugin(PLUGIN_NAME, InstanceReuseMode.Reuse, null);
   }

   @Override
   public void startPluginForTheFirstTime (SrummyUIListener listener) {
      //different method for doing extra things if necessary, 
      Message m = Message.builder("reetiIP")
            .add("address", "130.215.28.4").build();
      dispatcher.send(m);
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
   public void makeBoardPlayable () {
      if ( gameState.didAnyOneJustWin() == 0 ) {
         Message m = Message.builder(MSG_BOARD_PLAYABILITY)
               .add("value", "true").build();
         dispatcher.send(m);
      }
   }

   @Override
   public void makeBoardUnplayable () {
      Message m = Message.builder(MSG_BOARD_PLAYABILITY)
            .add("value", "false").build();
      dispatcher.send(m);
   }

   @Override
   public void reset () {
      gameState = new SrummyGameState();
      SrummyClient.gameRound = 0;
      latestAgentMove = latestHumanMove = null;
      humanPlayedMoves = new ArrayList<SrummyLegalMove>();
      Message m = Message.builder(MSG_RESET_GAME)
            .build();
      dispatcher.send(m);
   }

}
