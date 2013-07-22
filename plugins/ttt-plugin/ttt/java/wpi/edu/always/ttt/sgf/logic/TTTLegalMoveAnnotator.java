package wpi.edu.always.ttt.sgf.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.LegalMoveAnnotator;

public class TTTLegalMoveAnnotator implements LegalMoveAnnotator{

   double[] annotationBoard = {.5, .5, .5, .5, .5, .5, .5, .5, .5};

   //turn:: 1:user, 2:agent
   public List<AnnotatedLegalMove> annotate(
         List<TTTLegalMove> moves, TTTGameState state, int turn){

      List<AnnotatedLegalMove> annotatedMoves = 
            new ArrayList<AnnotatedLegalMove>();

      //annotation board, default value is 0.5
      for(int i = 0; i < 9; i ++)
         annotationBoard[i] = 0.5;

      //user#:: (1: user, 2: agent), diagonal# (1: left to right, 2: else)
      Map<Integer, Integer> rowWinChances = new HashMap<Integer, Integer>(); //row# to user#
      Map<Integer, Integer> colWinChances = new HashMap<Integer, Integer>(); //col# to user#
      Map<Integer, Integer> diaWinChances = new HashMap<Integer, Integer>(); //diagonal# to user#

      //check horizontal winning chance (2 finish) for any player!
      for(int i = 0; i < 9; i += 3){
         if(state.board[i] == state.board[i + 1] && state.board[i] != 0 && state.board[i + 2] == 0)
            rowWinChances.put(i+2, state.board[i]);
         if(state.board[i] == state.board[i + 2] && state.board[i] != 0 && state.board[i + 1] == 0)
            rowWinChances.put(i+1, state.board[i]);
         if(state.board[i + 1] == state.board[i + 2] && state.board[i + 1] != 0 && state.board[i] == 0)
            rowWinChances.put(i, state.board[i + 1]);
      }

      //check vertical winning chance (2 finish) for any player!
      for(int i = 0; i < 3; i ++){
         if(state.board[i] == state.board[i + 3] && state.board[i] != 0 && state.board[i + 6] == 0)
            colWinChances.put(i+6, state.board[i]);
         if(state.board[i] == state.board[i+6] && state.board[i] != 0 && state.board[i + 3] == 0)
            colWinChances.put(i+3, state.board[i]);
         if(state.board[i + 3] == state.board[i + 6] && state.board[i + 3] != 0 && state.board[i] == 0)
            colWinChances.put(i, state.board[i + 3]);
      }

      //check diagonal (left to right) winning chance (2 finish) for any player!
      if(state.board[0] == state.board[4] && state.board[0] != 0 && state.board[8] == 0)
         diaWinChances.put(8, state.board[0]);
      if(state.board[0] == state.board[8] && state.board[0] != 0 && state.board[4] == 0)
         diaWinChances.put(4, state.board[0]);
      if(state.board[4] == state.board[8] && state.board[4] != 0 && state.board[0] == 0)
         diaWinChances.put(0, state.board[4]);

      //check diagonal (right to left) winning chance (2 finish) for any player!
      if(state.board[2] == state.board[4] && state.board[2] != 0 && state.board[6] == 0)
         diaWinChances.put(6, state.board[2]);
      if(state.board[2] == state.board[6] && state.board[2] != 0 && state.board[4] == 0)
         diaWinChances.put(4, state.board[2]);
      if(state.board[4] == state.board[6] && state.board[4] != 0 && state.board[2] == 0)
         diaWinChances.put(2, state.board[4]);		


      /* Assigning the annotation values based on the policy below:
       * 
       * first all cells have .5 by default, 
       * so all later values are overwritten.
       * 
       * if no winning opportunity{
       * 		center cell, if available: .7
       * 		corners, if any available: .6
       * }
       * if finishing cell to win for agent{
       * 		that cell: .9
       * 		any other cell: .2
       * 		corners, if any available: .3
       * 		center cell, if available: .4
       * }
       * if finishing cell to win for human{
       * 		that cell: 1
       * 		any other cell: 0
       * }
       */

      //to also have all together
      Map<Integer, Integer> allWinChances = new HashMap<Integer, Integer>(); //row# to user#
      allWinChances.putAll(rowWinChances);
      allWinChances.putAll(colWinChances);
      allWinChances.putAll(diaWinChances);

      //if no winning opportunity, according to the above...
      if(allWinChances.isEmpty()){
         annotationBoard[0] = .6;
         annotationBoard[2] = .6;
         annotationBoard[6] = .6;
         annotationBoard[8] = .6;
         annotationBoard[4] = .7;
      }

      //if there is >= 1 winning opportunities
      else{
         for(int i = 0; i < 9; i ++){
            if(allWinChances.containsKey(i)){
               if(allWinChances.get(i) == 2){
                  annotationBoard[i] = .9;
                  for(int j = 0; j < 9; j ++)
                     if(j != i)
                        annotationBoard[j] = .2;
                  if(i != 0) annotationBoard[0] = .3;
                  if(i != 2) annotationBoard[2] = .3;
                  if(i != 6) annotationBoard[6] = .3;
                  if(i != 8) annotationBoard[8] = .3;
                  if(i != 4) annotationBoard[4] = .4;
               }
               if(allWinChances.get(i) == 1){
                  annotationBoard[i] = 1;
                  for(int j = 0; j < 9; j ++)
                     if(j != i)
                        annotationBoard[j] = 0;
               }
            }
         }
      }

      //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>	
      //OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH 
      //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

      /* assigning the annotated values based on the rules below:
       * if finishing cell to win for either of users, 1.
       * else 
       * 		if center cell, 0.7
       * 		if corner, 0.6
       * any other, 0.3
       * 
       * PLEASE NOTE: all cells are annotated, obviously only 
       * available cells actually get back with annotation.
       */
      /*In this loop, for "any user", if there exists in any horizontal, vertical or diagonal shapes 
		a wining chance, put a 1 annotation value for that cell in annotation map.
		for(int i = 0; i < 9; i++){
			if(rowWinChances.containsKey(i) ||
					colWinChances.containsKey(i) ||
						diaWinChances.containsKey(i))
				annotationBoard[i] = 1;
		}
		//for the remaining cells, if not conflicting, according to the comments above...
		if(!rowWinChances.containsKey(4) &&
				!colWinChances.containsKey(4) &&
					!diaWinChances.containsKey(4))
			annotationBoard[4] = .7;
		//for still remaining cells, if not conflicting, according to the comments above...
		if(!rowWinChances.containsKey(0) &&
				!colWinChances.containsKey(0) &&
					!diaWinChances.containsKey(0))
			annotationBoard[0] = .6;
		if(!rowWinChances.containsKey(2) &&
				!colWinChances.containsKey(2) &&
					!diaWinChances.containsKey(2))
			annotationBoard[0] = .6;
		if(!rowWinChances.containsKey(6) &&
				!colWinChances.containsKey(6) &&
					!diaWinChances.containsKey(6))
			annotationBoard[0] = .6;
		if(!rowWinChances.containsKey(8) &&
				!colWinChances.containsKey(8) &&
					!diaWinChances.containsKey(8))
			annotationBoard[0] = .6;
       */

      //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<	
      //OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH OLD APPROACH 
      //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

      //Building annotated moves based on annotation map and input available moves, legal moves.
      for(TTTLegalMove move : moves)
         annotatedMoves.add(new TTTAnnotatedLegalMove(move, annotationBoard[move.cellNumber]));

      return annotatedMoves;

   }

   protected void visualize(){

      TTTGameState testState = new TTTGameState();
      TTTLegalMoveGenerator testmg = new TTTLegalMoveGenerator();
      testState.board[0] = 0;
      testState.board[1] = 0;
      testState.board[2] = 0;
      testState.board[3] = 2;
      testState.board[4] = 0;
      testState.board[5] = 0;
      testState.board[6] = 2;
      testState.board[7] = 1;
      testState.board[8] = 0;
      List<TTTLegalMove> testLegalMoves = testmg.generate(testState);
      annotate(testLegalMoves, testState, 1);

      for(int i = 0; i < 9; i ++){
         if(i%3 == 0){
            System.out.print("\n");
            System.out.println(" ----------- ");
            System.out.print("|");
         }
         System.out.print(annotationBoard[i] + "|");
      }
      System.out.print("\n -----------");
   }

   public static void main(String[] args) {
      new TTTLegalMoveAnnotator().visualize();
   }

}
