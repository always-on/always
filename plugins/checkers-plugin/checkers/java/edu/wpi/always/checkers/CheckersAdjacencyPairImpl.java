package edu.wpi.always.checkers;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class CheckersAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<CheckersStateContext>
implements CheckersUIListener {

   public CheckersAdjacencyPairImpl(String message, CheckersStateContext context) {
      super(message, context);
   }

   public CheckersAdjacencyPairImpl (String message,
         CheckersStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   public void skipTo (AdjacencyPair nextAdjacencyPair) {
      setNextState(nextAdjacencyPair);
   }
   
   @Override
   public void humanPlayed() {
      afterLimbo();
   }
   protected void afterLimbo(){}
   
   @Override
   public void humanCommentTimeOut(){
      afterTimeOut();
   }
   protected void afterTimeOut(){}

   @Override
   public void agentPlayDelayOver() {
      afterAgentPlayDelay();
   }
   protected void afterAgentPlayDelay(){}
   
   @Override
   public void agentPlayingGazeDelayOver () {
      afterAgentPlayingGazeDelay();
   }
   protected void afterAgentPlayingGazeDelay(){}
   
   @Override
   public void nextState () {
      goToNextState();      
   }
   public void goToNextState(){}


}
