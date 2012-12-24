package edu.wpi.always.cm.dialog;

import edu.wpi.always.cm.utils.RArgumentNullException;

public class MultithreadAdjacencyPair<C> extends AdjacencyPairImpl<C> {

   public MultithreadAdjacencyPair (String message, C context) {
      super(message, context);
   }

   public MultithreadAdjacencyPair (String message, C context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   private AdjacencyPair abruptNextState = null;

   @Override
   public boolean prematureEnd () {
      return abruptNextState != null;
   }

   public void setNextState (AdjacencyPair state) {
      if ( state == null )
         throw new RArgumentNullException("state");
      abruptNextState = state;
   }

   @Override
   public AdjacencyPair nextState (String text) {
      if ( abruptNextState == null )
         return super.nextState(text);
      return abruptNextState;
   }
}
