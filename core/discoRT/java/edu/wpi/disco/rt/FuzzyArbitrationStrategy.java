package edu.wpi.disco.rt;

import java.io.InputStream;
import java.util.List;

public class FuzzyArbitrationStrategy implements ArbitrationStrategy {

   public static final double SWITCH_THRESHOLD = 0.501;
   private String fclDefinition;

   public FuzzyArbitrationStrategy () {
      fclDefinition = loadFuzzyRules();
   }

   public static String loadFuzzyRules () {
      String path = "/resources/arb.fcl";
      InputStream stream = Arbitrator.class.getResourceAsStream(path);
      if ( stream == null )
         throw new FuzzyException(
               "Cannot load fuzzy arbitration rules from resource: " + path);
      return Utils.readAllText(stream);
   }

   @Override
   public CandidateBehavior decide (List<CandidateBehavior> candidates,
         CandidateBehavior focusedOne) {
      if ( candidates == null || candidates.isEmpty() )
         return null;
      CandidateBehavior best = null;
      FuzzyArbitration fuzzy;
      if ( focusedOne == null )
         focusedOne = mostSpecificOne(candidates);
      fuzzy = new FuzzyArbitration(fclDefinition, focusedOne.getMetadata());
      double max = -1;
      for (CandidateBehavior c : candidates) {
         if ( c == focusedOne )
            continue;
         if ( c.getBehavior().isEmpty() )
            continue;
         BehaviorMetadata metadata = c.getMetadata();
         double sw = fuzzy.shouldSwitch(metadata);
         if ( sw > max ) {
            max = sw;
            best = c;
         }
      }
      if ( max > SWITCH_THRESHOLD )
         return best;
      return focusedOne;
   }

   private CandidateBehavior mostSpecificOne (List<CandidateBehavior> candidates) {
      if ( candidates == null )
         return null;
      CandidateBehavior selected = null;
      double max = -1;
      for (CandidateBehavior b : candidates) {
         if ( b.getMetadata().getSpecificity() > max ) {
            max = b.getMetadata().getSpecificity();
            selected = b;
         }
      }
      return selected;
   }

   @Override
   public CandidateBehavior decide (List<CandidateBehavior> candidates) {
      return decide(candidates, null);
   }
}
