package edu.wpi.disco.rt.perceptor;


public abstract class Perception {

   protected final long stamp;
   
   protected Perception () { stamp = System.currentTimeMillis(); }
   
   public long getTimeStamp () { return stamp; }
   
   public boolean isAfter (Perception perception) {
      return stamp > perception.stamp;
   }
}
