package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.schema.Schema;


public interface BehaviorProposalReceiver {

   void add (Schema schema, Behavior behavior, BehaviorMetadata metadata);
}
