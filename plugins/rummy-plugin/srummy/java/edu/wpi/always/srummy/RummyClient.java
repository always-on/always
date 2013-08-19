package edu.wpi.always.srummy;

import com.google.gson.JsonObject;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.always.srummy.game.Card;
import edu.wpi.always.srummy.game.DiscardMove;
import edu.wpi.always.srummy.game.GameState;
import edu.wpi.always.srummy.game.LayoffMove;
import edu.wpi.always.srummy.game.Meld;
import edu.wpi.always.srummy.game.MeldMove;
import edu.wpi.always.srummy.game.Move;
import edu.wpi.always.srummy.game.Player;
import edu.wpi.always.srummy.sgf.logic.RummyLegalMoveAnnotator;
import edu.wpi.always.srummy.sgf.logic.LegalMoveFetcher;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import edu.wpi.sgf.comment.Comment3;
import edu.wpi.sgf.comment.CommentingManager3;
import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.scenario.MoveChooser;
import edu.wpi.sgf.scenario.ScenarioFilter;
import edu.wpi.sgf.scenario.ScenarioManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

//SRUMMY, delete this cm later

public class RummyClient implements ClientPlugin {

	private static final String MSG_AVAILABLE_ACTION = "rummy.available_action";
	private static final String MSG_ALL_AVAILABLE_MOVES = "rummy.available_moves";//
	private static final String MSG_HUMAN_MOVE = "rummy.human_move";//?
	private static final String MSG_MOVE_HAPPENED = "rummy.move_happened";
	private static final String MSG_SGF_MOVE = "rummy.sgf_move";//
	private static final String MSG_GAME_STATE = "rummy.game_state";//
	private static final String MSG_USER_MOVE = "rummy.user+move";//
	private static final String MSG_STATE_CHANGED = "rummy.state_changed";
	private final UIMessageDispatcher dispatcher;
	private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<Message>();
	private TimeStampedValue<String> availableMove = null;
	private TimeStampedValue<String> userMove = null;
	private BehaviorBuilder lastMoveProposal;
	private DateTime myLastMoveTime;
	Boolean userWon = null;
	private boolean reactedToFinishedGameAlready = false;
	private int agentCardsNum = 10;
	private int userCardsNum = 10;

	private List<Move> possibleMoves;
	List<AnnotatedLegalMove> annotatedMoves;
	List<AnnotatedLegalMove> passedMoves;
	private LegalMoveFetcher moveFetcher;
	private RummyLegalMoveAnnotator moveAnnotator;
	private ScenarioManager scenarioManager;
	private ScenarioFilter scenarioFilter;
	private MoveChooser moveChooser;
	private CommentingManager3 commentingManager;
	private GameState gameState;
	private List<Move> humanPlayedMoves;
	private int hashCodeOfTheSelectedMove;
	private int numOfPossibleDiscards
	, numOfPossibleMelds, numOfPossibleLayoffs;

	/**
	 * For user turn reminder (in millis).
	 */
	public static int TIMEOUT_DELAY = MenuTurnStateMachine.TIMEOUT_DELAY/2; // *3 

	private long waitingForUserSince; // millis or zero if not waiting
	private boolean yourTurn;  // last proposal (not done)

	public RummyClient (UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		registerHandlerFor(MSG_AVAILABLE_ACTION);
		registerHandlerFor(MSG_MOVE_HAPPENED);
		registerHandlerFor(MSG_STATE_CHANGED);
		//mine
		registerHandlerFor(MSG_GAME_STATE);
		registerHandlerFor(MSG_USER_MOVE);
		registerHandlerFor(MSG_ALL_AVAILABLE_MOVES);
		registerHandlerFor(MSG_SGF_MOVE);

		//>>
		possibleMoves = new ArrayList<Move>();
		annotatedMoves = new ArrayList<AnnotatedLegalMove>();
		passedMoves = new ArrayList<AnnotatedLegalMove>();
		moveFetcher = new LegalMoveFetcher();
		moveAnnotator = new RummyLegalMoveAnnotator();
		scenarioManager = new ScenarioManager();
		commentingManager = new CommentingManager3();
		scenarioFilter = new ScenarioFilter();
		moveChooser = new MoveChooser();
		gameState = new GameState();
		humanPlayedMoves = new ArrayList<Move>();
		hashCodeOfTheSelectedMove = 0;
		
		scenarioManager.chooseOrUpdateScenario();
		
		//<<
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

	protected void receivedMessage (Message message) {
		inbox.add(message);
	}

	@Override
	public void initInteraction () {
		Message params = Message.builder("params").add("first_move", "agent")
				.build();
		Message m = Message.builder("start_plugin").add("name", "rummy")
				.add(params).build();
		sendToEngine(m);
	}

	boolean gameOver () {
		return userWon != null;
	}

	@Override
	public BehaviorBuilder updateInteraction (boolean lastProposalIsDone, double focusMillis) {
		if ( lastProposalIsDone && yourTurn ) {
			yourTurn = false;
			waitingForUserSince = System.currentTimeMillis();
		}

		String comment = "";
		comment = processInbox();

		// always propose at least an empty menu for extension
		ProposalBuilder builder = newProposal();
		BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
		metadata.timeRemaining(agentCardsNum + userCardsNum);
		metadata.specificity(ActivitySchema.SPECIFICITY);
		builder.setMetadata(metadata);
		// don't want to mistake lastProposalIsDone with one about a _move_,
		// hence the check for lastMoveProposal not being null
		if ( gameOver() && lastMoveProposal == null ) {
			if ( !lastProposalIsDone && !reactedToFinishedGameAlready ) {
				if ( userWon ) {
					builder.say("You won. Oh, well.");
				} else {
					builder.say("Yay! I won!");
				}
				builder.metadataBuilder().timeRemaining(0);
			} else {
				reactedToFinishedGameAlready = true;
			}
			agentMove();
			return builder;
		}

		if ( lastMoveProposal != null && lastProposalIsDone )
			myLastMoveTime = DateTime.now();
		if ( lastMoveProposal != null && !lastProposalIsDone ) {
			agentMove();
			return lastMoveProposal;
		} else if ( availableMove != null && availableMove.getValue().length() > 0 ) {
			PluginSpecificBehavior move = new PluginSpecificBehavior(this,
					// for printing only, action name not used in doAction below
					availableMove.getValue() + "@" + availableMove.getTimeStamp(),
					AgentResources.HAND);
			String toSay = "";
//			if ( isMeld(availableMove.getValue()) ) {
//				toSay = "Now, I am going to do this meld, $ and done!";         
//			} else if ( isDraw(availableMove.getValue()) ) {
//				// toSay = "Okay, I have to draw a card. <GAZE DIR=AWAY/> $ The Card is drawn, and let me see what I can do with it! <GAZE DIR=TOWARD/>";
//				toSay = "Okay, I have to draw a card. $ The Card is drawn, and let me see what I can do with it!";
//			} else if ( isDiscard(availableMove.getValue()) ) {
//				toSay = "I am done, so I'll discard this one, $ and now it's your turn.";
//			}
			//say SGF comment
			toSay = comment;
			
			SyncSayBuilder b = new SyncSayBuilder(toSay, move,
					// following behavior is unconstrained (live at start)
					// for menu extension if any 
					MenuBehavior.EMPTY);
			b.setMetaData(metadata);
			lastMoveProposal = b;
			availableMove = null;
			agentMove();

			return b;
		} else {
			lastMoveProposal = null;
			if ( userMadeAMeldAfterMyLastMove() ) {
				builder.say("Good one!");
				agentMove();
				return builder;
			}
		}

		if ( yourTurn  
				|| (waitingForUserSince > 0 
						&& ( (System.currentTimeMillis() - waitingForUserSince) > TIMEOUT_DELAY))
						|| (focusMillis > DiscoRT.ARBITRATOR_INTERVAL*5) ) {
			builder.say("It's your turn");      
			yourTurn = true;
		} else {
			if ( waitingForUserSince == 0 ) waitingForUserSince = System.currentTimeMillis();
			builder.showMenu(Collections.<String>emptyList(), false);
		}
		//**** never returns just null anymore--always empty menu
		// see RummySchema for idle behavior (if builder returns Behavior.NULL)
		return builder;
	}

	private void agentMove () {
		yourTurn = false;
		waitingForUserSince = 0;
	}

	private boolean userMadeAMeldAfterMyLastMove () {
		return userMove != null
				&& userMove.getTimeStamp().isAfter(myLastMoveTime)
				&& isMeld(userMove.getValue());
	}

	private boolean isDiscard (String value) {
		return value.equals("discard");
	}

	private boolean isDraw (String value) {
		return value.equals("draw");
	}

	private boolean isMeld (String value) {
		return value.equals("meld");
	}

	private ProposalBuilder newProposal () {
		return new ProposalBuilder(this); 
	}

	private String processInbox () {
		String comment = "";
		while (!inbox.isEmpty()) {
			Message m = inbox.poll();

			//investigate order more,. matters? 
			if(m.getType().equals(MSG_ALL_AVAILABLE_MOVES)) {
				comment = processInbox2(m);
			}
			if( m.getType().equals(MSG_GAME_STATE) ) {
				try {gameState.synchGame(m);
				} catch (Exception e) {e.printStackTrace();}
			}
			if( m.getType().equals(MSG_USER_MOVE) ) {
				try {processUserMove(m);
				} catch (Exception e) {e.printStackTrace();}
			}
			if ( m.getType().equals(MSG_AVAILABLE_ACTION) ) {
				String action = m.getBody().get("action").getAsString();
				availableMove = new TimeStampedValue<String>(action);
			} 
			if ( m.getType().equals(MSG_MOVE_HAPPENED) ) {
				if ( m.getBody().get("player").getAsString().equals("user") ) {
					String move = m.getBody().get("move").getAsString();
					userMove = new TimeStampedValue<String>(move);
				}
			}
			if ( m.getType().equals(MSG_STATE_CHANGED) ) {
				String newState = m.getBody().get("state").getAsString();
				if ( newState.equals("user_won") ) {
					userWon = true;
				} else if ( newState.equals("agent_won") ) {
					userWon = false;
				}
				agentCardsNum = m.getBody().get("agent_cards").getAsInt();
				userCardsNum = m.getBody().get("user_cards").getAsInt();
			}
		}
		return comment;
	}

	private String processInbox2(Message msg){

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

		//fetch moves, annotate, filter by scenarios. send the selected one
		/*note: moveFetcher will keep a up-to-date copy of the current move
		//options, also for possible use of other parts of the framework.*/
		moveFetcher.fetchMoves(possibleMoves);
		annotatedMoves.addAll(moveAnnotator
				.annotate(LegalMoveFetcher.currentPossibleMoves));
		passedMoves.addAll(scenarioFilter.filter(
				annotatedMoves, scenarioManager.getCurrentScenario()));
		
		AnnotatedLegalMove selectedMove =
				moveChooser.choose(passedMoves);
		
		if(selectedMove == null)
			return "";
			
		/*Finding the chosen move in the initial list to send its hashCode back
		 * 0 is sent if draw move was the option. Later for cheating the hashcode of
		 * the chosen card.*/
		hashCodeOfTheSelectedMove = 0;
		for(Move eachMove : possibleMoves)
			if(eachMove.equals(selectedMove.getMove()))
				hashCodeOfTheSelectedMove = eachMove.hashCode();
		if(hashCodeOfTheSelectedMove == 0){
			try {throw new Exception(
					"Chosen Move apparantly not in the initial Moves!");
			} catch (Exception e) {e.printStackTrace();}
		}
		
		//debugging log
		System.out.println("\n\n******HASH\n\n+ " +
				hashCodeOfTheSelectedMove + 
				"\n\n******HASH");
		
		//****************tickAll()
		scenarioManager.tickAll();
		
		//make the comment on own move, based on annotations and scenario
		Comment3 cm = null;
		cm = commentingManager
				.pickCommentOnOwnMove(selectedMove);
		if(cm != null)
			return cm.getContent();
		
		return "";
		
		//			System.out.println("*******************");
		//			System.out.println(moveAnnotator.toString(a));
		//			System.out.println("*******************");
		//
		//			System.out.println("\n***************************\n");
		//			for(Move eachMove : possibleMoves)
		//				System.out.println(eachMove.toString() + "\n");
		//			System.out.println("\n***************************\n");


		//		if(msg.getType().equals(MSG_HUMAN_MOVE)){
		//
		//		}


	}

	private void processUserMove(Message humanMoveAsMessage) throws Exception {

		JsonObject humanMoveAsJsonObj = 
				humanMoveAsMessage.getBody();
		
		if(humanMoveAsJsonObj == null)
			throw new Exception(
					"Human Move not received correctly.");
		
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
		//check all above in this method	
		//currently not sending/receiving draw, necessary? 
		//ASSUMING MAKING COMMENTS 
	}
	
	@Override
	public void endInteraction () {
		Message m = Message.builder("stop_plugin").add("name", "rummy").build();
		sendToEngine(m);
	}

	@Override
	public void doAction (String actionName) { 
		Message m = Message.builder(MSG_SGF_MOVE)
				.add("hashcode", hashCodeOfTheSelectedMove)
				.build();
		sendToEngine(m);
	}

	private void sendToEngine (Message m) {
		dispatcher.send(m);
	}
}
