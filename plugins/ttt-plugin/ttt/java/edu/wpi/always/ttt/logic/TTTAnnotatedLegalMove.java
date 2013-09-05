package edu.wpi.always.ttt.logic;

import edu.wpi.sgf.logic.AnnotatedLegalMove;
import edu.wpi.sgf.logic.LegalMove;

public class TTTAnnotatedLegalMove extends AnnotatedLegalMove{

   public TTTAnnotatedLegalMove(LegalMove move, double moveStrength){
      super(move, moveStrength);
   }

}
