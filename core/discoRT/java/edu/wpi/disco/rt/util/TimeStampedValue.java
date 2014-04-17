package edu.wpi.disco.rt.util;

import org.joda.time.DateTime;

public class TimeStampedValue<T> {

   private final long timeStamp;
   private final T val;

   public TimeStampedValue (T value) {
      timeStamp = System.currentTimeMillis();
      this.val = value;
   }

   public T getValue () {
      return val;
   }

   public long getTimeStamp () {
      return timeStamp;
   }

   @Override
   public String toString () {
      return val + ":" + timeStamp;
   }
}
