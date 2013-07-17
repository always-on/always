package wpi.edu.always.ttt.sgf.logic;

import java.util.ArrayList;
import java.util.List;

public class TTTLegalMoveGenerator {

   public List<TTTLegalMove> generate(TTTGameState state) {
      List<TTTLegalMove> legalMoves = new ArrayList<TTTLegalMove>();
      for(int i = 0; i < 9; i++)
         if(state.board[i] == 0)
            legalMoves.add(new TTTLegalMove(i));

      return legalMoves;
   }

}
