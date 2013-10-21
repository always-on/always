package edu.wpi.always.checekrs.logic;

import java.util.*;
import edu.wpi.sgf.logic.GameLogicState;

/** 
 * Class of the state of the Checkers game
 * @author Morteza Behrooz
 * @version 1.0
 */

public class CheckersGameState extends GameLogicState{

   private static final int AGENT_IDENTIFIER = 2;


   /*
    * if a winner, returns the winner number (1 user, 2 agent), tie 3, else 0;
    */
   public int didAnyOneJustWin(){
      return 0;

   }

   private boolean boardIsFull(){
      return false;
   }

   public boolean isItATie(){
      return false;
   }

   public void resetBoard(){
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

      return gameSpecificTags;
   }

   public void updateLastBoardState(){
   }

}
