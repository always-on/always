package wpi.edu.always.ttt.sgf.logic;
//package wpi.edu.always.tictactoe.sgf.logic;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import edu.wpi.sgf.comment.CommentingManager;
//import edu.wpi.sgf.scenario.ScenarioManager;
//
///* This class gathers the data from all logic components, 
// * executes the moves, passes the effects for logic state, 
// * keeps a history of game states and generally 'runs' the game.
// */
//public class Play {
//
//	private int turn; //2 is agent, 1 is user
//	private TTTLegalMove userMove;
//	private TTTGameState state;
//	private ScenarioManager scenarioManager;
//	private CommentingManager commentingManager;
//	private TTTLegalMoveAnnotator annotator;
//	private TTTLegalMoveGenerator generator;
//
//	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//
//	public boolean processUserMove(TTTUserRequestedMove reqMvoe){
//		if(0 <= reqMvoe.cellNumber && reqMvoe.cellNumber < 9)
//			if(state.board[reqMvoe.cellNumber] == 0)
//				return true;
//		return false;
//	}
//
//	public void execute(TTTAnnotatedLegalMove pickedAnnotatedMove, TTTGameState state){
//		if(state.board[pickedAnnotatedMove.getMove().cellNumber] == 0)
//			state.board[pickedAnnotatedMove.getMove().cellNumber] = turn;
//	}
//
//	private void visualize(String commentToMake){
//		System.out.print("\nGAME BOARD: (" + 
//				scenarioManager.getCurrentScenario().getName() + " scenario)");
//		state.visualize();
//		System.out.println("\nCOMMENT: " + commentToMake);
//		System.out.println("\n---------------------------\n");
//		try {
//			Runtime.getRuntime().exec("clear");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void play () {
//
//		state = new TTTGameState();
//		annotator = new TTTLegalMoveAnnotator();
//		generator = new TTTLegalMoveGenerator();
//		scenarioManager = new ScenarioManager();
//		commentingManager = new CommentingManager();
//		TTTAnnotatedLegalMove move2make = null;
//		Comment pickedComment = null;
//		turn = 1;
//
//		while(!state.userWins && !state.agentWins && !state.tie){
//
//			if(turn == 1){
//				System.out.print("User move: ");
//				try {
//					TTTUserRequestedMove reqMove = new TTTUserRequestedMove(Integer.valueOf(
//							String.valueOf((reader.readLine().trim().charAt(0)))));
//					if(processUserMove(reqMove))
//						userMove = reqMove.confirm();
//				} catch (IOException e) {
//					System.out.println("input err");
//					e.printStackTrace();
//				}
//				List<TTTLegalMove> userMoveInAWrapperList = new ArrayList<TTTLegalMove>();
//				userMoveInAWrapperList.add(userMove);
//				move2make = annotator.annotate
//						(userMoveInAWrapperList, state, turn).get(0);
//				
//			}
//
//			//Agent turn
//			if(turn == 2){
//				List<TTTLegalMove> moves = generator.generate(state);
//				scenarioManager.chooseOrUpdateScenario();
//				List<TTTAnnotatedLegalMove> annMoves = annotator.annotate(moves, state, turn);
//				List<TTTAnnotatedLegalMove> fittingMoves = new ArrayList<TTTAnnotatedLegalMove>();
//
//				for(TTTAnnotatedLegalMove annMove : annMoves)
//					if(scenarioManager.getCurrentScenario().evaluate(annMove, true))
//						fittingMoves.add(annMove);
//
//				if(fittingMoves.isEmpty()){
//					System.out.println("Unable to follow the scenario, scenario has to change now...");
//					System.exit(0);
//				}else{
//					Collections.shuffle(fittingMoves);
//					//to be replicable maybe below, argument has 2 b fixed
//					//move2make = fittingMoves.get(new Random().nextInt(fittingMoves.size() - 1));
//					move2make = fittingMoves.get(0);
//				}
//				
//				//picks the comment for own move
//				pickedComment = commentingManager.pickCommentOnOwnMove(
//						state, scenarioManager.getCurrentScenario(), move2make, turn);
//
//				//increases the scenario progress
//				scenarioManager.getCurrentScenario().tick();
//			}
//
//			execute(move2make, state);
//			
//			if(pickedComment != null){
//				//show board and comment
//				visualize(pickedComment.getContent());
//				try {
//					commentingManager.make(pickedComment);
//				} catch (IllegalArgumentException | InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			else
//				visualize("");
//
//			//check if anyone won the game, or if a tie
//			int potentialWinner = state.didAnyOneJustWin();
//			if(potentialWinner == 1) {
//				state.userWins = true;
//				System.out.println("(Human won)");
//			}
//			if(potentialWinner == 2){
//				state.agentWins = true;
//				System.out.println("(Agent won)");
//			}
//			if(state.isItATie()){
//				state.tie = true;
//				System.out.println("(tie)");
//			}			
//
//			//turn changes
//			turn = 3 - turn;
//			pickedComment = null;
//		}
//	}
//
//	public static void main(String[] args) {
//		new Play().play();
//	}
//
//}
