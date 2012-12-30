package edu.wpi.always.cm.primitives;

import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.realizer.PrimitiveBehavior;
import java.awt.Point;

public class GazeBehavior extends PrimitiveBehavior {

   private final Point point;

   // TODO: change to polar coordinates?
   public GazeBehavior (Point m) {
      this.point = m;
   }

   @Override
   public Resource getResource () {
      return AgentResources.GAZE;
   }

   public Point getPoint () {
      return point;
   }

   @Override
   public boolean equals (Object o) {
      if ( this == o )
         return true;
      if ( !(o instanceof GazeBehavior) )
         return false;
      GazeBehavior theOther = (GazeBehavior) o;
      return theOther.point.equals(this.point);
   }

   @Override
   public int hashCode () {
      return point.hashCode();
   }

   @Override
   public String toString () {
      return "GAZE(" + point + ')';
   }
}
