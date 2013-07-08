package wpi.edu.always.tictactoe;

import edu.wpi.always.cm.ProposalBuilder;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.disco.rt.behavior.*;

public class TicTacToeSchema extends ActivitySchema {

   private final TicTacToeClient plugin;
   private boolean firstRun = true;

   public TicTacToeSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, TicTacToeClient plugin) {
      super(behaviorReceiver, resourceMonitor);
      this.plugin = plugin;
   }

   @Override
   public void run () {
      if ( firstRun ) {
         firstRun = false;
         plugin.initInteraction();
      }
      BehaviorBuilder r = plugin.updateInteraction(lastProposalIsDone(), 
            getFocusMillis());
      BehaviorMetadata m = r.getMetadata();
      Behavior b = r.build();
      if ( b.equals(Behavior.NULL) && !plugin.gameOver() ) {
         propose(new ProposalBuilder().idle().build(), ActivitySchema.SPECIFICITY);
      } else {
         propose(b, m);
      }
   }
   
   // always adds focus (see RummyClient.updateInteraction)
   @Override
   public void setNeedsFocusResource (boolean focus) {} 
}
