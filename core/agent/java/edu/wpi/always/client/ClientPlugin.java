package edu.wpi.always.client;

import edu.wpi.always.cm.BehaviorBuilder;

public interface ClientPlugin {

   void doAction (String actionName);

   void initInteraction ();

   BehaviorBuilder updateInteraction (boolean lastProposalIsDone);

   void endInteraction ();
}
