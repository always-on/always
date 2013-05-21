package edu.wpi.disco.rt.menu;

public class NullMenuTimeoutHandler implements MenuTimeoutHandler {

   @Override
   public AdjacencyPair handle (AdjacencyPair original) {
      return null;
   }
}
