package edu.wpi.always.ttt.sgf.logic;

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
         List<TTTLegalMove> moves, TTTGameState state){

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
       * 		that cell: 1
       * 		any other cell: .2
       * 		corners, if any available: .3
       * 		center cell, if available: .4
       * }
       * if finishing cell to win for human{
       * 		that cell: 0.9
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
                  annotationBoard[i] = 1;
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
                  annotationBoard[i] = .9;
                  for(int j = 0; j < 9; j ++)
                     if(j != i)
                        annotationBoard[j] = 0;
               }
            }
         }
      }

      //Building annotated moves based on annotation map and input available moves, legal moves.
      for(TTTLegalMove move : moves)
         annotatedMoves.add(new TTTAnnotatedLegalMove(move, annotationBoard[move.cellNumber]));

      return annotatedMoves;

   }
   
   public AnnotatedLegalMove annotate(
         TTTLegalMove move, TTTGameState state){
      
      List<TTTLegalMove> moveAsList = new ArrayList<TTTLegalMove>();
      moveAsList.add(move);
      
      return annotate(moveAsList, state).get(0);
      
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
      annotate(testLegalMoves, testState);

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
