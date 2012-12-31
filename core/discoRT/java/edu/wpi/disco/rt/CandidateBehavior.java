package edu.wpi.disco.rt;

import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.schema.Schema;


public class CandidateBehavior {

   private final Behavior behavior;
   private final Schema proposer;
   private final BehaviorMetadata meta;

   public CandidateBehavior (Behavior behavior, Schema proposer,
         BehaviorMetadata meta) {
      this.behavior = behavior;
      this.proposer = proposer;
      this.meta = meta;
   }

   public Behavior getBehavior () {
      return behavior;
   }

   public Schema getProposer () {
      return proposer;
   }

   public BehaviorMetadata getMetadata () {
      return meta;
   }

   @Override
   public String toString () {
      return "<" + proposer + ": " + behavior + ">";
   }
}
