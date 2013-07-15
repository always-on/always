package wpi.edu.always.ttt;

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
import edu.wpi.sgf.logic.LegalMove;
import edu.wpi.sgf.scenario.MoveChooser;
import edu.wpi.sgf.scenario.ScenarioFilter;
import edu.wpi.sgf.scenario.ScenarioManager;

import org.joda.time.DateTime;

import wpi.edu.always.ttt.sgf.logic.TTTGameState;
import wpi.edu.always.ttt.sgf.logic.TTTLegalMove;
import wpi.edu.always.ttt.sgf.logic.TTTLegalMoveAnnotator;
import wpi.edu.always.ttt.sgf.logic.TTTLegalMoveGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TicTacToeClient implements ClientPlugin {

	private static final String MSG_HUMAN_MOVE = "tictactoe.human_played_cell";
	private static final String MSG_AGENT_MOVE = "tictactoe.agent_cell";

	private final UIMessageDispatcher dispatcher;
	private final ConcurrentLinkedQueue<Message> inbox = new ConcurrentLinkedQueue<Message>();
	private TimeStampedValue<String> availableMove = null;
	private BehaviorBuilder lastMoveProposal;
	private DateTime myLastMoveTime;
	Boolean userWon = null;
	private boolean reactedToFinishedGameAlready = false;

//	List<AnnotatedLegalMove> annotatedMoves;
//	List<AnnotatedLegalMove> passedMoves;
	private TTTLegalMoveGenerator moveGenerator;
	private TTTLegalMoveAnnotator moveAnnotator;
	private ScenarioManager scenarioManager;
	private ScenarioFilter scenarioFilter;
	private MoveChooser moveChooser;
	private CommentingManager commentingManager;
	private TTTGameState gameState;
	private List<LegalMove> humanPlayedMoves;

	/**
	 * For user turn reminder (in millis).
	 */
	public static int TIMEOUT_DELAY = MenuTurnStateMachine.TIMEOUT_DELAY;

	private long waitingForUserSince; // millis or zero if not waiting
	private boolean yourTurn;  // last proposal (not done)
	public TicTacToeClient (UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		registerHandlerFor(MSG_HUMAN_MOVE);
		registerHandlerFor(MSG_AGENT_MOVE);

		moveGenerator = new TTTLegalMoveGenerator();
		moveAnnotator = new TTTLegalMoveAnnotator();
//		annotatedMoves = new ArrayList<AnnotatedLegalMove>();
//		passedMoves = new ArrayList<AnnotatedLegalMove>();
		commentingManager = new CommentingManager();
		scenarioFilter = new ScenarioFilter();
		moveChooser = new MoveChooser();
		scenarioManager = new ScenarioManager();
		gameState = new TTTGameState();
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
			builder.say("Play now or give up!");      
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
				cellNum = Integer.valueOf(m.getBody().get("cellNum").getAsString());
				gameState.board[cellNum - 1] = 1;
				System.out.println(cellNum);
				
				AnnotatedLegalMove dmove = 
//						moveChooser.choose(scenarioFilter.filter(
//								moveAnnotator.annotate(moveGenerator.generate(
//										gameState), gameState, 2), scenarioManager.getCurrentScenario()));
				moveChooser.choose(
						moveAnnotator.annotate(moveGenerator.generate(
								gameState), gameState, 2));
				
				
				gameState.board[((TTTLegalMove)dmove.getMove()).getCellNum()] = 2;
				
				scenarioManager.tickAll();
				
				Comment cm = commentingManager.pickCommentOnOwnMove(
						gameState, scenarioManager.getCurrentActiveScenarios() , dmove, 2);
				
				int winner = gameState.didAnyOneJustWin();
				if(winner == 2)
					gameState.agentWins = true;
				if(winner == 1)
					gameState.userWins = true;
				if(winner == 3)
					gameState.tie = true;
				
				comment = cm.getContent();

				Message m2 = Message.builder(MSG_AGENT_MOVE)
						.add("cellNum", ((TTTLegalMove)dmove.getMove()).getCellNum() + 1)
						.build();
				sendToEngine(m2);
				availableMove = 
						new TimeStampedValue<String>(String.valueOf(
								((TTTLegalMove)dmove.getMove()).getCellNum() + 1));
			}

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

	}

	private void sendToEngine (Message m) {
		dispatcher.send(m);
	}
}
