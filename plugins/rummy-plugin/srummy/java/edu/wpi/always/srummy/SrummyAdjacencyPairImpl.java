package edu.wpi.always.srummy;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class SrummyAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<SrummyStateContext>
implements SrummyUIListener {

   public SrummyAdjacencyPairImpl(
         String message, SrummyStateContext context) {
      super(message, context);
      this.repeatOption = false;
   }

   public SrummyAdjacencyPairImpl (String message,
         SrummyStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
      this.repeatOption = false;
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
   public void receivedAgentMoveOptions(String moveType) {
      agentMoveOptionsReceived(moveType);
   }
   protected void agentMoveOptionsReceived(String moveType){}
   
   @Override
   public void humanCommentTimeOut(){
      afterTimeOut();
   }
   protected void afterTimeOut(){}

   @Override 
   public void agentDrawDelayOver(){
      aferAgentDrawDelay();
   }
   protected void aferAgentDrawDelay(){}
   
   @Override
   public void agentDiscardDelayOver(){
      afterAgentDiscardOrMeldLayoffDelay();
   }
   protected void afterAgentDiscardOrMeldLayoffDelay(){}
   
   @Override
   public void agentPlayDelayOver(){
      afterAgentPlayingDelay();
   }
   protected void afterAgentPlayingDelay(){}
   
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

   @Override
   public void waitingForAgentDrawOptionsOver () {
      timesUpForDrawOption();
   }
   protected void timesUpForDrawOption(){}
   
   @Override
   public void gameIsOverByYieldingZeroCardsInATurn(){
      gameOverByZeroCardsInThisTurn();
   }
   protected void gameOverByZeroCardsInThisTurn(){}
   
}
