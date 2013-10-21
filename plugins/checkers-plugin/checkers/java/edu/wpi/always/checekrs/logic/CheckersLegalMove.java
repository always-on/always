package edu.wpi.always.checekrs.logic;

import edu.wpi.sgf.logic.LegalMove;

public class CheckersLegalMove implements LegalMove{

   int cellNumber;

   public CheckersLegalMove(int cellNumber){
      this.cellNumber = cellNumber;
   }

   public int getCellNum(){
      return cellNumber;
   }

}
