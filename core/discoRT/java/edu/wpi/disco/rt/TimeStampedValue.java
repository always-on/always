package edu.wpi.disco.rt;

import org.joda.time.DateTime;

public class TimeStampedValue<T> {

   private final DateTime timeStamp;
   private final T val;

   public TimeStampedValue (T value, DateTime timeStamp) {
      this.timeStamp = timeStamp;
      this.val = value;
   }

   public TimeStampedValue (T value) {
      this(value, DateTime.now());
   }

   public T getValue () {
      return val;
   }

   public DateTime getTimeStamp () {
      return timeStamp;
   }

   @Override
   public String toString () {
      return val + ":" + timeStamp;
   }
}
