package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;
import org.joda.time.DateTime;
import java.awt.Point;

public class MovementPerception extends Perception {

   private final boolean moving;
   private final Point point;

   public MovementPerception (DateTime stamp, boolean moving, Point point) {
      super(stamp);
      this.moving = moving;
      this.point = point;
   }

   public MovementPerception (boolean moving, Point point) {
      this(DateTime.now(), moving, point);
   }

   public Point getPoint () {
      return point;
   }

   public boolean isMoving () {
      return moving;
   }

   @Override
   public String toString () {
      return "Movement[stamp=" + stamp + ", moving=" + moving
         + ", point=" + point + "]";
   }
   
}
