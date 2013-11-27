package edu.wpi.disco.rt.menu;

import edu.wpi.disco.rt.util.NullArgumentException;

public class MultithreadAdjacencyPair<C extends AdjacencyPair.Context> extends AdjacencyPairBase<C> {

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
         throw new NullArgumentException("state");
      abruptNextState = state;
   }

   @Override
   public AdjacencyPair nextState (String text) {
      if ( abruptNextState == null )
         return super.nextState(text);
      return abruptNextState;
   }
}
