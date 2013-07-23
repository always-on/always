package wpi.edu.always.ttt.sgf.logic;

import edu.wpi.sgf.logic.GameLogicState;

/** 
 * Class of the state of the TicTacToe game
 * @author Morteza Behrooz
 * @version 1.5
 */

public class TTTGameState extends GameLogicState{

   //public int[] board = {1, 1, 2, 0, 0, 2, 1, 0, 0};
   public int[] board = {0, 0, 0, 0, 0, 0, 0, 0, 0};

   /*
    * if a winner, returns the winner number (1 user, 2 agent), tie 3, else 0;
    */
   public int didAnyOneJustWin(){

      //checks for a winner horizontally.
      for (int k = 1; k < 3; k++){
         for (int i = 0; i < 7; i+=3){
            if(board[i] == k && board[i + 1] == k && board[i + 2] == k)
               return k;
         }
      }

      //checks for a winner vertically.
      for (int k = 1; k < 3; k++){
         for (int i = 0; i < 3; i++){
            if(board[i] == k && board[i + 3] == k && board[i + 6] == k)
               return k;
         }
      }

      //checks for a winner diagonally, left to right.
      for (int i = 1; i < 3; i++){
         if(board[0] == i && board[4] == i && board[8] == i){
            return i;
         }
      }

      //checks for winner diagonally, right to left.
      for (int i = 1; i < 3; i++){
         if(board[2] == i && board[4] == i && board[6] == i){
            return i;
         }
      }

      //checks for a tie
      boolean full = true;
      for (int i = 0; i < 9; i ++)
         if(board[i] == 0)
            full = false;
      if(full)      
         return 3;

      //else
      return 0;
   }

   private boolean boardIsFull(){
      for(int i : board)
         if(i == 0)
            return false;
      return true;
   }

   public boolean isItATie(){
      if(didAnyOneJustWin() == 0 && boardIsFull())
         return true;
      return false;
   }

   public void visualize(){

      for(int i = 0; i < 9; i ++){
         if(i%3 == 0){
            System.out.print("\n");
            System.out.println(" ----------- ");
            System.out.print("|");
         }
         if(board[i] == 0)
            System.out.print("   " + "|");
         if(board[i] == 2)
            System.out.print(" O " + "|");
         if(board[i] == 1)
            System.out.print(" X " + "|");
      }
      System.out.print("\n -----------");

   }

   public static void main(String[] args) {
      new TTTGameState() {
      }.visualize();
   }
}
