package edu.wpi.always.checekrs.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.LegalMoveAnnotator;

public class CheckersLegalMoveAnnotator implements LegalMoveAnnotator{

   double[] annotationBoard = {.5, .5, .5, .5, .5, .5, .5, .5, .5};

   public List<AnnotatedLegalMove> annotate(
         List<CheckersLegalMove> moves, CheckersGameState state){

      List<AnnotatedLegalMove> annotatedMoves = 
            new ArrayList<AnnotatedLegalMove>();

      return annotatedMoves;

   }
   
   public AnnotatedLegalMove annotate(
         CheckersLegalMove move, CheckersGameState state){
      
      List<CheckersLegalMove> moveAsList = new ArrayList<CheckersLegalMove>();
      moveAsList.add(move);
      
      return annotate(moveAsList, state).get(0);
      
   }

   protected void visualize(){


   }

   public static void main(String[] args) {
      new CheckersLegalMoveAnnotator().visualize();
   }

}
