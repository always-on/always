package edu.wpi.always.cm.perceptors;

import java.awt.Point;

import org.joda.time.DateTime;

import edu.wpi.disco.rt.perceptor.Perception;

public class MovementPerception extends Perception {

   private final boolean moving;
   private final Point point;

   public MovementPerception (boolean moving, Point point) {
      this.moving = moving;
      this.point = point;
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
