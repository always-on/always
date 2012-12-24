package edu.wpi.disco.rt;

import java.util.List;

public interface ArbitrationStrategy {

   CandidateBehavior decide (List<CandidateBehavior> candidates);

   CandidateBehavior decide (List<CandidateBehavior> candidates,
         CandidateBehavior focusedOne);
}
