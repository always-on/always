package wpi.edu.always.ttt;

import edu.wpi.disco.rt.menu.AdjacencyPair;
import edu.wpi.disco.rt.menu.MultithreadAdjacencyPair;

public class TTTAdjacencyPairImpl 
extends	MultithreadAdjacencyPair<TTTStateContext>
implements TTTUIListener {

   public TTTAdjacencyPairImpl(String message, TTTStateContext context) {
      super(message, context);
   }

   public TTTAdjacencyPairImpl (String message,
         TTTStateContext context, boolean twoColumn) {
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
   public void afterTimeOut(){}

   @Override
   public void agentPlayDelayOver() {
      afterAgentPlayDelay();
   }
   public void afterAgentPlayDelay(){}
   
   
}
