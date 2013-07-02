package edu.wpi.always.srummy;

import edu.wpi.always.cm.ProposalBuilder;
import edu.wpi.always.cm.schemas.ActivitySchema;
import edu.wpi.disco.rt.behavior.*;

//SRUMMY, delete this cm later

public class RummySchema extends ActivitySchema {

   private final RummyClient plugin;
   private boolean firstRun = true;

   public RummySchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory resourceMonitor, RummyClient plugin) {
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
}
