package edu.wpi.always.cm.dialog;

public class NullMenuTimeoutHandler implements MenuTimeoutHandler {

   @Override
   public AdjacencyPair handle (AdjacencyPair original) {
      return null;
   }
}
