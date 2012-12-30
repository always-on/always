package edu.wpi.disco.rt.perceptor;

import org.joda.time.DateTime;

public abstract class Perception {

   protected final DateTime stamp;
   
   protected Perception (DateTime stamp) { this.stamp = stamp; }
   
   public DateTime getTimeStamp () {
      return stamp;
   }
}
