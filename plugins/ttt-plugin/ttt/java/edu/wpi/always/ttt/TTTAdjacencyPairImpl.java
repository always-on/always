package edu.wpi.always.ttt;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class TTTAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<TTTStateContext>
implements TTTUIListener {

   public TTTAdjacencyPairImpl(String message, TTTStateContext context) {
      super(message, context);
      this.repeatOption = false;
   }

   public TTTAdjacencyPairImpl (String message,
         TTTStateContext context, boolean twoColumn) {
      super(message, context, twoColumn);
      this.repeatOption = false;
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
