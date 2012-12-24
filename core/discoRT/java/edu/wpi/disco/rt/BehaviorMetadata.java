package edu.wpi.disco.rt;

public class BehaviorMetadata {

   private final double specificity;
   private final double dueIn;
   private final double timeRemaining;
   private final double newActivity;
   private final double container;
   private final double goodInterruptMoment;
   private final double timeActive;

   public BehaviorMetadata (double specificity, double dueIn,
         double timeRemaining, boolean newActivity, boolean isContainer,
         boolean goodInterruptMoment, double timeActive) {
      this.specificity = specificity;
      this.dueIn = dueIn;
      this.timeRemaining = timeRemaining;
      this.timeActive = timeActive;
      this.goodInterruptMoment = goodInterruptMoment ? 1 : 0;
      this.container = isContainer ? 1 : 0;
      this.newActivity = newActivity ? 1 : 0;
   }

   public double getSpecificity () {
      return specificity;
   }

   public double getDueIn () {
      return dueIn;
   }

   public double getTimeRemaining () {
      return timeRemaining;
   }

   public boolean getNewActivity () {
      return newActivity > 0.5 ? true : false;
   }

   public boolean getIsContainer () {
      return container > 0.5 ? true : false;
   }

   public boolean getGoodInterruptMoment () {
      return goodInterruptMoment > 0.5 ? true : false;
   }

   public double getTimeActive () {
      return timeActive;
   }

   @Override
   public String toString () {
      String s = "{";
      s += "due : " + dueIn;
      s += ", time : " + timeRemaining;
      s += ", spec : " + specificity;
      s += ", new : " + newActivity;
      s += ", container : " + container;
      s += ", interrupt : " + goodInterruptMoment;
      s += ", timeActive : " + timeActive;
      s += "}";
      return s;
   }
}