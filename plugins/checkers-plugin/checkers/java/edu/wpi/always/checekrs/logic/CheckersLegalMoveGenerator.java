package edu.wpi.always.checekrs.logic;

import java.util.List;

public class CheckersLegalMoveGenerator {

   private static final int AGENT_IDENTIFIER = 2; //black
   
   public List<CheckersLegalMove> generate(CheckersGameState state) {

      //using the method in GameState class
      return state.getLegalMoves(AGENT_IDENTIFIER);
      
   }
   
}
