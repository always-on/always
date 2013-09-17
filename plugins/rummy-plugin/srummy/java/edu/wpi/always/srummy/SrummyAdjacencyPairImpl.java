package edu.wpi.always.srummy;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class SrummyAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<SrummyStateContext>
implements SrummyUIListener {

   public SrummyAdjacencyPairImpl(
         String message, SrummyStateContext context) {
      super(message, context);
   }

   public SrummyAdjacencyPairImpl (String message,
         SrummyStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
   }

   public void skipTo (AdjacencyPair nextAdjacencyPair) {
      setNextState(nextAdjacencyPair);
   }
   
   @Override
   public void receivedNewState(){
      newStateReceived();
   }
   protected void newStateReceived(){}
   
   @Override
   public void receivedHumanMove() {
      humanMoveReceived();
   }
   protected void humanMoveReceived(){}
   
   @Override
   public void receivedAgentMoveOptions() {
      agentMoveOptionsReceived();
   }
   protected void agentMoveOptionsReceived(){}
   
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
