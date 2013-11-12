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
   public void receivedHumanMove() {
      humanMoveReceived();
   }
   protected void humanMoveReceived(){}

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
   protected void goToNextState(){}

   @Override
   public void shouldHaveJumped () {
      tellShouldHaveJumped();
   }
   protected void tellShouldHaveJumped(){}

   //   @Override
   //   public void receivedNewState() {
   //   }

}
