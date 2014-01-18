package edu.wpi.always.checkers.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.scenario.MoveChooser;

public class CheckersMoveChooser extends MoveChooser{

   private static AnnotatedLegalMove moveMemory;

   public AnnotatedLegalMove choose(List<AnnotatedLegalMove> someMoves, boolean multiJump){

      List<AnnotatedLegalMove> candidates = 
            new ArrayList<AnnotatedLegalMove>();
   
      if(someMoves == null ||
            someMoves.isEmpty())
         return null;

      Collections.sort(someMoves);

      double maxStrength = someMoves.get(
            someMoves.size() - 1).getMoveStrength();
   
      for(AnnotatedLegalMove eachMove : someMoves)
         if(eachMove.getMoveStrength() >= maxStrength)
            candidates.add(eachMove);

      Collections.shuffle(candidates);

      AnnotatedLegalMove selected = null;
      
      if(multiJump){
         for(AnnotatedLegalMove each : candidates){
            if(((CheckersLegalMove)each.getMove()).fromRow 
                  == ((CheckersLegalMove)moveMemory.getMove()).toRow &&
                  ((CheckersLegalMove)each.getMove()).fromCol 
                  == ((CheckersLegalMove)moveMemory.getMove()).toCol){
               selected = each;
               break;
            }
         }
      }
      else
         selected = candidates.get(
               new Random().nextInt(candidates.size()));

      moveMemory = selected;
      return selected;
      
   }
}
