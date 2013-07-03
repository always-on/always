package wpi.edu.always.tictactoe;

import com.google.gson.JsonObject;
import edu.wpi.always.client.*;
import edu.wpi.always.cm.*;
import edu.wpi.always.cm.primitives.*;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.disco.rt.DiscoRT;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import edu.wpi.sgf.comment.Comment;
import edu.wpi.sgf.comment.CommentingManager;
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

public class TicTacToeClient implements ClientPlugin {

	private static final String MSG_HUMAN_MOVE = "tictactoe.human_played_cell";
	private static final String MSG_AGENT_MOVE = "tictactoe.agent_cell";

	private final UIMessageDispatcher dispatcher;
	private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<Message>();
	private TimeStampedValue<String> availableMove = null;
//	private TimeStampedValue<String> userMove = null;
	private BehaviorBuilder lastMoveProposal;
	private DateTime myLastMoveTime;
	Boolean userWon = null;
	private boolean reactedToFinishedGameAlready = false;
//	private int agentCardsNum = 10;
//	private int userCardsNum = 10;

//	private List<Move> possibleMoves;
//	List<AnnotatedLegalMove> annotatedMoves;
//	List<AnnotatedLegalMove> passedMoves;
//	private LegalMoveFetcher moveFetcher;
//	private RummyLegalMoveAnnotator moveAnnotator;
//	private ScenarioManager scenarioManager;
//	private ScenarioFilter scenarioFilter;
//	private MoveChooser moveChooser;
//	private CommentingManager commentingManager;
//	private GameState gameState;
//	private List<Move> humanPlayedMoves;
//	private int numOfPossibleDiscards
//	, numOfPossibleMelds, numOfPossibleLayoffs;

	/**
	 * For user turn reminder (in millis).
	 */
	public static int TIMEOUT_DELAY = MenuTurnStateMachine.TIMEOUT_DELAY/1; // *3 //*2 

	private long waitingForUserSince; // millis or zero if not waiting
	private boolean yourTurn;  // last proposal (not done)
	private int board[] = {0,0,0,0,0,0,0,0,0};
	public TicTacToeClient (UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		registerHandlerFor(MSG_HUMAN_MOVE);
		registerHandlerFor(MSG_AGENT_MOVE);

		//>>
//		possibleMoves = new ArrayList<Move>();
//		annotatedMoves = new ArrayList<AnnotatedLegalMove>();
//		passedMoves = new ArrayList<AnnotatedLegalMove>();
//		commentingManager = new CommentingManager();
//		scenarioFilter = new ScenarioFilter();
//		moveChooser = new MoveChooser();
//		hashCodeOfTheSelectedMove = 0;
//		
//		scenarioManager.chooseOrUpdateScenario();
		
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
//		Message params = Message.builder("params").add("first_move", "agent")
//				.build();
		Message m = Message.builder("start_plugin").add("name", "tictactoe")
				//.add(params)
				.build();
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

		ProposalBuilder builder = newProposal();
		BehaviorMetadataBuilder metadata = new BehaviorMetadataBuilder();
		metadata.specificity(ActivitySchema.SPECIFICITY);
		builder.setMetadata(metadata);
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
					availableMove.getValue() + "@" + availableMove.getTimeStamp(),
					AgentResources.HAND);
			String toSay = "";
			toSay = comment;
			
			SyncSayBuilder b = new SyncSayBuilder(toSay, move,
					MenuBehavior.EMPTY);
			b.setMetaData(metadata);
			lastMoveProposal = b;
			availableMove = null;
			agentMove();

			return b;
		} else {
			lastMoveProposal = null;

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
		return builder;
	}

	private void agentMove () {
		yourTurn = false;
		waitingForUserSince = 0;
	}

	private ProposalBuilder newProposal () {
		return new ProposalBuilder(this); 
	}

	private String processInbox () {
		String comment = "";
		while (!inbox.isEmpty()) {
			Message m = inbox.poll();
			int cellNum;
			if(m.getType().equals(MSG_HUMAN_MOVE)) {
				cellNum=Integer.valueOf(m.getBody().get("cellNum").getAsString());
				board[cellNum - 1] = 2;
				System.out.println(cellNum);
			}
			doAction("");
		}
		return comment;
	}
	
	@Override
	public void endInteraction () {
		Message m = Message.builder("stop_plugin").add("name", "tictactoe").build();
		sendToEngine(m);
	}

	@Override
	public void doAction (String actionName) { 
		Message m = Message.builder(MSG_AGENT_MOVE)
				.add("cellNum", "9")
				.build();
		sendToEngine(m);
	}

	private void sendToEngine (Message m) {
		dispatcher.send(m);
	}
}
