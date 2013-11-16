package edu.wpi.always.checkers.logic;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.LegalMoveAnnotator;

/**
 * For checkers game, annotating is simple since if you can jump you have to.
 * Jump moves get 0.9, normal moves 0.5
 * If you get to the last row and no jump, 0.7
 */
public class CheckersLegalMoveAnnotator implements LegalMoveAnnotator{

   
   public List<AnnotatedLegalMove> annotate(
         List<CheckersLegalMove> moves, CheckersGameState state){

      List<AnnotatedLegalMove> annotatedMoves = 
            new ArrayList<AnnotatedLegalMove>();
      
      double ann = .5;
      for (CheckersLegalMove each : moves){
         if(each.isJump()) ann = .9; else ann = .5;
         annotatedMoves.add(new AnnotatedLegalMove(each, ann));
      }
      
      return annotatedMoves;
   }
   
   public AnnotatedLegalMove annotate(
         CheckersLegalMove move, CheckersGameState state){
      
      List<CheckersLegalMove> moveAsList = 
            new ArrayList<CheckersLegalMove>();
      moveAsList.add(move);
      
      return annotate(moveAsList, state).get(0);
   }

   protected void visualize(){

   }

   public static void main(String[] args) {
      new CheckersLegalMoveAnnotator().visualize();
   }

}
