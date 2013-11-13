package edu.wpi.always.checekrs.logic;

import java.util.*;
import edu.wpi.always.checkers.CheckersClient;
import edu.wpi.sgf.logic.*;

/** 
 * Class of the state of the Checkers game
 * @author Morteza Behrooz
 * @version 2.0
 */

public class CheckersGameState extends GameLogicState{

   private static final int RED = 1; //user
   private static final int RED_KING = 3; //user crown
   private static final int BLACK = 2; //agent
   private static final int BLACK_KING = 4; //agent crown
   private static final int EMPTY = 0; //empty
   private boolean agentJustJumped = false;
   private boolean userJustJumped = false;
   
   public int[][] board = new int[8][8];

   public CheckersGameState(){
      setUpBoard();
   }
   
   public void setUpBoard(){
      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
            if ( row % 2 != col % 2 ) {
               if (row < 3)
                  board[row][col] = RED;
               else if (row > 4)
                  board[row][col] = BLACK;
               else
                  board[row][col] = EMPTY;
            }
            else {
               board[row][col] = EMPTY;
            }
         }
      }
   }
   
   /**
    * Returns a list containing all the CheckersLegalMoves
    * for the specified player on the current board.  If the player
    * has no legal moves, null is returned.  The value of player
    * should be one of the constants RED or BLACK; if not, null
    * is returned.  If the returned value is non-null, it consists
    * entirely of jump moves or entirely of regular moves, since
    * if the player can jump, only jumps are legal moves.
    */
   List<CheckersLegalMove> getLegalMoves(int player) {
      
      if (player != RED && player != BLACK)
         return null;
      
      int playerKing;  // The constant representing a King belonging to player.
      if (player == RED)
         playerKing = RED_KING;
      else
         playerKing = BLACK_KING;
      
      ArrayList<CheckersLegalMove> moves = new ArrayList<CheckersLegalMove>();  // Moves will be stored in this list.
      
      /*  First, check for any possible jumps.  Look at each square on the board.
       If that square contains one of the player's pieces, look at a possible
       jump in each of the four directions from that square.  If there is 
       a legal jump in that direction, put it in the moves ArrayList.
       */
      
      for (int row = 0; row < 8; row++) {
         for (int col = 0; col < 8; col++) {
            if (board[row][col] == player || board[row][col] == playerKing) {
               if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                  moves.add(new CheckersLegalMove(row, col, row+2, col+2));
               if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                  moves.add(new CheckersLegalMove(row, col, row-2, col+2));
               if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                  moves.add(new CheckersLegalMove(row, col, row+2, col-2));
               if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                  moves.add(new CheckersLegalMove(row, col, row-2, col-2));
            }
         }
      }
      
      /*  If any jump moves were found, then the user must jump, so we don't 
       add any regular moves.  However, if no jumps were found, check for
       any legal regular moves.  Look at each square on the board.
       If that square contains one of the player's pieces, look at a possible
       move in each of the four directions from that square.  If there is 
       a legal move in that direction, put it in the moves ArrayList.
       */
      
      if (moves.size() == 0) {
         for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
               if (board[row][col] == player || board[row][col] == playerKing) {
                  if (canMove(player,row,col,row+1,col+1))
                     moves.add(new CheckersLegalMove(row,col,row+1,col+1));
                  if (canMove(player,row,col,row-1,col+1))
                     moves.add(new CheckersLegalMove(row,col,row-1,col+1));
                  if (canMove(player,row,col,row+1,col-1))
                     moves.add(new CheckersLegalMove(row,col,row+1,col-1));
                  if (canMove(player,row,col,row-1,col-1))
                     moves.add(new CheckersLegalMove(row,col,row-1,col-1));
               }
            }
         }
      }
      
      /* If no legal moves have been found, return null.  Otherwise, create
       an array just big enough to hold all the legal moves, copy the
       legal moves from the ArrayList into the array, and return the array. */
      
      if (moves.size() == 0)
         return null;
      return moves;
      
   }
   
   /**
    * This is called by the getLegalMoves() method to determine whether
    * the player can legally move from (r1,c1) to (r2,c2).  It is
    * assumed that (r1,c1) contains one of the player's pieces and
    * that (r2,c2) is a neighboring square.
    */
   private boolean canMove(int player, int r1, int c1, int r2, int c2) {
      
      if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
         return false;  // (r2,c2) is off the board.
      
      if (board[r2][c2] != EMPTY)
         return false;  // (r2,c2) already contains a piece.
      
      if (player == RED) {
         if (board[r1][c1] == RED && r2 < r1)
            return false;  // Regular red piece can only move down.
         return true;  // The move is legal.
      }
      else {
         if (board[r1][c1] == BLACK && r2 > r1)
            return false;  // Regular black piece can only move up.
         return true;  // The move is legal.
      }
      
   } 
   
   /**
    * Return a list of the legal jumps that the specified player can
    * make starting from the specified row and column.  If no such
    * jumps are possible, null is returned.  The logic is similar
    * to the logic of the getLegalMoves() method.
    */
   List<CheckersLegalMove> getLegalJumpsFrom(int player, int row, int col) {
      if (player != RED && player != BLACK)
         return null;
      int playerKing;  // The constant representing a King belonging to player.
      if (player == RED)
         playerKing = RED_KING;
      else
         playerKing = BLACK_KING;
      List<CheckersLegalMove> moves = new ArrayList<CheckersLegalMove>();  // The legal jumps will be stored in this list.
      if (board[row][col] == player || board[row][col] == playerKing) {
         if (canJump(player, row, col, row+1, col+1, row+2, col+2))
            moves.add(new CheckersLegalMove(row, col, row+2, col+2));
         if (canJump(player, row, col, row-1, col+1, row-2, col+2))
            moves.add(new CheckersLegalMove(row, col, row-2, col+2));
         if (canJump(player, row, col, row+1, col-1, row+2, col-2))
            moves.add(new CheckersLegalMove(row, col, row+2, col-2));
         if (canJump(player, row, col, row-1, col-1, row-2, col-2))
            moves.add(new CheckersLegalMove(row, col, row-2, col-2));
      }
      if (moves.size() == 0)
         return null;
      return moves;
   } 

   /**
    * This is called by the two previous methods to check whether the
    * player can legally jump from (r1,c1) to (r3,c3).  It is assumed
    * that the player has a piece at (r1,c1), that (r3,c3) is a position
    * that is 2 rows and 2 columns distant from (r1,c1) and that 
    * (r2,c2) is the square between (r1,c1) and (r3,c3).
    */
   private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {
      
      if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
         return false;  // (r3,c3) is off the board.
      
      if (board[r3][c3] != EMPTY)
         return false;  // (r3,c3) already contains a piece.
      
      if (player == BLACK) {
         if (board[r1][c1] == BLACK && r3 > r1)
            return false;  // Regular black piece can only move up.
         if (board[r2][c2] != RED && board[r2][c2] != RED_KING)
            return false;  // There is no black piece to jump.
         return true;  // The jump is legal.
      }
      else {
         if (board[r1][c1] == RED && r3 < r1)
            return false;  // Regular red piece can only move down.
         if (board[r2][c2] != BLACK && board[r2][c2] != BLACK_KING)
            return false;  // There is no red piece to jump.
         return true;  // The jump is legal.
      }
      
   }
   
   /**
    * Make the specified move.  It is assumed that move
    * is non-null and that the move it represents is legal.
    */
   void makeMove(CheckersLegalMove move) {
      makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
   }
   
   /**
    * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
    * assumed that this move is legal.  If the move is a jump, the
    * jumped piece is removed from the board.  If a piece moves to
    * the last row on the opponent's side of the board, the 
    * piece becomes a king.
    */
   void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
      board[toRow][toCol] = board[fromRow][fromCol];
      board[fromRow][fromCol] = EMPTY;
      if (fromRow - toRow == 2 || fromRow - toRow == -2) {
         // The move is a jump.  Remove the jumped piece from the board.
         int jumpRow = (fromRow + toRow) / 2;  // Row of the jumped piece.
         int jumpCol = (fromCol + toCol) / 2;  // Column of the jumped piece.
         board[jumpRow][jumpCol] = EMPTY;
      }
      if (toRow == 0 && board[toRow][toCol] == BLACK)
         board[toRow][toCol] = BLACK_KING;
      if (toRow == 7 && board[toRow][toCol] == RED)
         board[toRow][toCol] = RED_KING;
   }
   
   /**
    * Performs agent's move
    */
   public void performAgentMove(CheckersLegalMove move){
      
      makeMove(move);
      
      /* Keep jumping if you have to!*/ //FORAGENT
     if (move.isJump()) {
        if(getLegalJumpsFrom(RED, move.toRow, move.toCol) != null)
           ;//new state? 
     }
      
      possibleWinner();
      
   }
   
   /** Confirm and perform user's requested move.
    * If not legal (not jumping while able to)
    * agent will say an appropriate prompt.*/
   public boolean performUserMove(CheckersLegalMove move){

      boolean could = false;

      // return false if user could jump but didn't
      for (int row = 0; row < 8; row++)
         for (int col = 0; col < 8; col++)
            if (board[row][col] == RED || board[row][col] == RED_KING) {
               if (canJump(RED, row, col, row+1, col+1, row+2, col+2)
                     || canJump(RED, row, col, row-1, col+1, row-2, col+2)
                     || canJump(RED, row, col, row+1, col-1, row+2, col-2)
                     || canJump(RED, row, col, row-1, col-1, row-2, col-2))
                  could = true;
            }

      if(could && !move.isJump())
         return false;

      // safe now
      makeMove(move);

      // also, agent should say a different thing if 
      // you just did not "continue" to jump. 
      // (further handled in adjacency pairs)
      if (move.isJump()) CheckersClient.
      userJumpedAtLeastOnceInThisTurn = true;

      possibleWinner();

      return true;

   }
   
   public int numberOfPiecesRemainingFor(int user){ 

      int count = 0;
      for(int i = 0; i < 8; i ++)
         for(int j = 0; j < 8; j ++)
            if(board[i][j] == user 
            || board[i][j] == user + 2)
               count++;

      return count;
   }
   
   /*
    * if a winner, returns the winner number (1 user, 2 agent) else 0;
    */
   public int possibleWinner(){
      
      int redCount = 0, blackCount = 0;
      for(int i = 0; i < 8; i ++){
         for(int j = 0; j < 8; j ++){
            if(board[i][j] == RED || board[i][j] == RED_KING) redCount++;
            if(board[i][j] == BLACK || board[i][j] == BLACK_KING) blackCount++;
         }
      }
            
      if(redCount == 0) { this.agentWins = true; return BLACK; }
      if(blackCount == 0) { this.userWins = true; return RED; }
      return EMPTY;

   }

   public void resetGame(){
     setUpBoard();
     resetGameStatus();
   }

   public void resetGameStatus(){
      userWins = agentWins = tie = false;
   }

   public List<String> getGameSpecificCommentingTags(
         CheckersLegalMove move, int player){

      List<String> gameSpecificTags = 
            new ArrayList<String>();
      
      if(move.isJump()) {
         
         if(player == RED /*user*/) 
         { 
            if(userJustJumped) 
               gameSpecificTags.add("humanCaptureALot");
            else {
               userJustJumped = true; 
               gameSpecificTags.add("humanCapture"); 
            }
         }
         if(player == BLACK /*agent*/) 
         {
            if(agentJustJumped) 
               gameSpecificTags.add("agentCaptureALot");
            else { 
               agentJustJumped = true; 
               gameSpecificTags.add("agentCapture"); 
            }
         }
      }
      else {
         userJustJumped = false;
         agentJustJumped = false;
      }
      
      if(player == RED /*user*/ && move.toRow == 7)
         gameSpecificTags.add("humanCrown");
      if(player == BLACK /*agent*/ && move.toRow == 0)
         gameSpecificTags.add("agentCrown");
      
      return gameSpecificTags;
   
   }

   public String makeMoveDesc (CheckersLegalMove move) {
      
      return String.valueOf(move.fromRow) + "," 
      + String.valueOf(move.fromCol) + "//" 
      + String.valueOf(move.toRow) + "," 
      + String.valueOf(move.toCol);

   }
   
   private void visualize () {
      for(int i = 0; i < 8; i ++){
         System.out.print(i+"|");
         for(int j = 0; j < 8; j++){
            System.out.print(board[i][j] + " ");
         }
         System.out.print("\n");
      }
   }
   
   //for testing
   public static void main (String[] args) {
      CheckersGameState cgs = new CheckersGameState();
      cgs.setUpBoard();
      cgs.visualize();
   }

}
