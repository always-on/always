package edu.wpi.always.checekrs.logic;

public class CheckersUserRequestedMove {

   protected int cellNumber;

   public CheckersUserRequestedMove(int cellNumber){
      this.cellNumber = cellNumber;
   }

   /**
    * Returns an instance of CheckersLegalMove 
    * After being checked by the caller 
    * that this move is legal according to 
    * the game state.
    * @return CheckersLegalMove
    */
   public CheckersLegalMove confirm(){
      return new 
            CheckersLegalMove(this.cellNumber);
   }
}
