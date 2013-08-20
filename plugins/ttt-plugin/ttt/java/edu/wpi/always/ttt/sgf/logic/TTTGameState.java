package edu.wpi.always.ttt.sgf.logic;

import java.util.*;
import edu.wpi.sgf.logic.GameLogicState;

/** 
 * Class of the state of the TicTacToe game
 * @author Morteza Behrooz
 * @version 1.5
 */

public class TTTGameState extends GameLogicState{

   private static final int AGENT_IDENTIFIER = 2;
   
   //public int[] board = {1, 1, 2, 0, 0, 2, 1, 0, 0}; //for test
   public int[] board = {0, 0, 0, 0, 0, 0, 0, 0, 0};
   private int[] lastBoardState = {0, 0, 0, 0, 0, 0, 0, 0, 0};

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
      for(int i = 0; i < 9; i ++)
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

   public void resetBoard(){
      for (int i = 0; i < 9; i ++)
            board[i] = 0;
   }
   
   public void resetGameStatus(){
      userWins = false;
      agentWins = false;
      tie = false;
   }
   
   public List<String> getGameSpecificCommentingTags(){
      
      //center cell taken tags
      List<String> gameSpecificTags = 
            new ArrayList<String>();
      if(lastBoardState[4] == 0
            && board[4] != 0){
         if(board[4] == AGENT_IDENTIFIER)
            gameSpecificTags.add("centerCellTakenByAgent");
         else
            gameSpecificTags.add("centerCellTakenByHuman");
      }
      
      //win opportunity blocked
      //>> check horizontal
      for(int i = 0; i < 9; i += 3){
         if(lastBoardState[i] == lastBoardState[i + 1] 
               && lastBoardState[i] != 0 
               && lastBoardState[i + 2] == 0 
               && board[i + 2] != 0
               && board[i + 2] != lastBoardState[i]){
            if(board[i + 2] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
         if(lastBoardState[i] == lastBoardState[i + 2] 
               && lastBoardState[i] != 0 
               && lastBoardState[i + 1] == 0
               && board[i + 1] != 0
               && board[i + 1] != lastBoardState[i]){
            if(board[i + 1] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
         if(lastBoardState[i + 1] == lastBoardState[i + 2] 
               && lastBoardState[i + 1] != 0 
               && lastBoardState[i] == 0
               && board[i] != 0
               && board[i] != lastBoardState[i + 1]){
            if(board[i] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
      }
      //<<
      //>> Check vertical 
      for(int i = 0; i < 3; i ++){
         if(lastBoardState[i] == lastBoardState[i + 3]
               && lastBoardState[i] != 0 
               && lastBoardState[i + 6] == 0
               && board[i + 6] != 0
               && board[i + 6] != lastBoardState[i]){
            if(board[i + 6] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
         if(lastBoardState[i] == lastBoardState[i + 6] 
               && lastBoardState[i] != 0 
               && lastBoardState[i + 3] == 0
               && board[i + 3] != 0
               && board[i + 3] != lastBoardState[i]){
            if(board[i + 3] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
         if(lastBoardState[i + 3] == lastBoardState[i + 6] 
               && lastBoardState[i + 3] != 0 
               && lastBoardState[i] == 0
               && board[i] != 0
               && board[i] != lastBoardState[i + 3]){
            if(board[i] == AGENT_IDENTIFIER)
               gameSpecificTags.add("HumanWinOppBlocked");
            else
               gameSpecificTags.add("AgentWinOppBlocked");
         }
      }
      //<<
      //>> Check diagonal 1
      if(lastBoardState[0] == lastBoardState[4] 
            && lastBoardState[0] != 0 
            && lastBoardState[8] == 0
            && board[8] != 0
            && board[8] != lastBoardState[0]){
         if(board[8] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      if(lastBoardState[0] == lastBoardState[8] 
            && lastBoardState[0] != 0 
            && lastBoardState[4] == 0
            && board[4] != 0
            && board[4] != lastBoardState[0]){
         if(board[4] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      if(lastBoardState[4] == lastBoardState[8] 
            && lastBoardState[4] != 0 
            && lastBoardState[0] == 0
            && board[0] != 0
            && board[0] != lastBoardState[4]){
         if(board[0] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      //<<
      //>> Check diagonal 2
      if(lastBoardState[2] == lastBoardState[4] 
            && lastBoardState[2] != 0 
            && lastBoardState[6] == 0
            && board[6] != 0
            && board[6] != lastBoardState[2]){
         if(board[6] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      if(lastBoardState[2] == lastBoardState[6] 
            && lastBoardState[2] != 0 
            && lastBoardState[4] == 0
            && board[4] != 0
            && board[4] != lastBoardState[2]){
         if(board[4] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      if(lastBoardState[4] == lastBoardState[6] 
            && lastBoardState[4] != 0 
            && lastBoardState[2] == 0
            && board[2] != 0
            && board[2] != lastBoardState[4]){
         if(board[2] == AGENT_IDENTIFIER)
            gameSpecificTags.add("HumanWinOppBlocked");
         else
            gameSpecificTags.add("AgentWinOppBlocked");
      }
      //<<
      
      return gameSpecificTags;
   }
   
   public void updateLastBoardState(){
      for(int i = 0; i < 9; i ++)
         lastBoardState[i] = board[i];
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
