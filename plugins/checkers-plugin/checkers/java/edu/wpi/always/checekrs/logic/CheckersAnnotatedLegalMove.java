package edu.wpi.always.checekrs.logic;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.LegalMove;

public class CheckersAnnotatedLegalMove extends AnnotatedLegalMove{

   public CheckersAnnotatedLegalMove(LegalMove move, double moveStrength){
      super(move, moveStrength);
   }

}
