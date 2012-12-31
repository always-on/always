package edu.wpi.always.test.cm;

import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.behavior.BehaviorMetadata;

public class Arbitrate {

   private static class Parameters {

      public double specificity;
      public double dueIn;
      public double timeRemaining;
      public boolean newActivity;
      public boolean isContainer;
      public boolean goodInterruptMoment = true;
      public double timeActive;

      public BehaviorMetadata toMetadata () {
         return new BehaviorMetadata(specificity, dueIn, timeRemaining,
               newActivity, isContainer, goodInterruptMoment, timeActive);
      }
   }

   private Parameters focus = new Parameters();
   private Parameters other = new Parameters();
   private boolean debugMode;

   public boolean switching () {
      return checkSwitching(true);
   }

   public boolean switchingIgnoreReverseStability () {
      return checkSwitching(false);
   }

   private boolean checkSwitching (boolean checkReverse) {
      BehaviorMetadata f = focus.toMetadata();
      BehaviorMetadata o = other.toMetadata();
      boolean shouldSwitch = shouldSwitch(f, o, false);
      if ( checkReverse ) {
         if ( shouldSwitch && shouldSwitch(o, f, true) ) {
            System.out
                  .println("UNSTABLE switch! Information about the reverse case follows:");
            shouldSwitch(o, f, false);
            throw new RuntimeException("swithing was not stable in this case");
         }
      }
      return shouldSwitch;
   }

   private boolean shouldSwitch (BehaviorMetadata f, BehaviorMetadata o,
         boolean silent) {
      FuzzyArbitration a = new FuzzyArbitration(
            FuzzyArbitrationStrategy.loadFuzzyRules(), f);
      a.setDebug(debugMode && !silent);
      if ( !silent ) {
         System.out.println();
         System.out.println();
         System.out.println(f + " vs. " + o);
      }
      double d = a.shouldSwitch(o);
      if ( !silent )
         System.out.println("   ====> " + d);
      return d > FuzzyArbitrationStrategy.SWITCH_THRESHOLD;
   }

   public void bothHaveSpecificityAndAreDueInWithRemainingTime (
         double specificity, double dueIn, double timeRemaining) {
      focus.specificity = specificity;
      focus.dueIn = dueIn;
      focus.timeRemaining = timeRemaining;
      focus.timeActive = 2;
      other.specificity = specificity;
      other.dueIn = dueIn;
      other.timeRemaining = timeRemaining;
      other.timeActive = 0;
   }

   public void theOtherHasSpecificity (double spec) {
      other.specificity = spec;
   }

   public void focusedOneHasRemainingTimeOf (double time) {
      focus.timeRemaining = time;
   }

   public void theOtherHasRemainingTimeOf (double time) {
      other.timeRemaining = time;
   }

   public void focusedOneHasSpecificity (double spec) {
      focus.specificity = spec;
   }

   public void theOtherIsANewActivity () {
      other.newActivity = true;
   }

   public void focusedOneIsContainer () {
      focus.isContainer = true;
   }

   public void focusedOneHasBeenActiveFor (double time) {
      focus.timeActive = time;
   }

   public void focusedOneIsNotContainer () {
      focus.isContainer = false;
   }

   public void theOtherIsDueIn (double time) {
      other.dueIn = time;
   }

   public void focusedOneIsDueIn (double time) {
      focus.dueIn = time;
   }

   public void swapTheTwo () {
      Parameters tmp = focus;
      focus = other;
      other = tmp;
   }

   public void debug (boolean b) {
      debugMode = b;
   }
}
