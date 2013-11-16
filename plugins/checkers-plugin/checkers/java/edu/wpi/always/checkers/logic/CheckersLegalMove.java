package edu.wpi.always.checkers.logic;

import edu.wpi.sgf.logic.LegalMove;

/**
 * A CheckersMove object represents a move in the game of Checkers.
 * It holds the row and column of the piece that is to be moved
 * and the row and column of the square to which it is to be moved.
 * (This class makes no guarantee that the move is actually legal.)   
 */
public class CheckersLegalMove 
implements LegalMove, Comparable<CheckersLegalMove> {

   int fromRow, fromCol;  // Position of piece to be moved.
   int toRow, toCol;      // Square it is to move to.
   public CheckersLegalMove(int r1, int c1, int r2, int c2) {
      // Constructor.  Just set the values of the instance variables.
      fromRow = r1;
      fromCol = c1;
      toRow = r2;
      toCol = c2;
   }
   boolean isJump() {
      // Test whether this move is a jump.  It is assumed that
      // the move is legal.  In a jump, the piece moves two
      // rows.  (In a regular move, it only moves one row.)
      return (fromRow - toRow == 2 || fromRow - toRow == -2);
   }
   
   @Override 
   public int compareTo(CheckersLegalMove that) {
    //> and < does not matter, written for semantic equality 
      if (this.fromRow == that.fromRow
            && this.fromCol == that.fromCol
            && this.toRow == that.toRow
            && this.toCol == that.toRow)
         return 0;
      return 1; 
   }

}
