package edu.wpi.always.cm.perceptors;

import org.joda.time.DateTime;
import java.awt.Point;

public class FacePerceptionImpl implements FacePerception {

   private final DateTime stamp;
   private final Point location;

   public FacePerceptionImpl (DateTime t, Point location) {
      this.stamp = t;
      this.location = location;
   }

   @Override
   public DateTime getTimeStamp () {
      return stamp;
   }

   @Override
   public Point faceLocation () {
      return location;
   }
}
