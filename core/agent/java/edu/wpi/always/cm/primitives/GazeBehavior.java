package edu.wpi.always.cm.primitives;

import edu.wpi.always.client.GazeRealizer;
import edu.wpi.disco.rt.Resource;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import java.awt.Point;

public class GazeBehavior extends PrimitiveBehavior {

   /**
    * Agent gaze for thinking (up and left)
    */
   public static final GazeBehavior THINKING = 
         new GazeBehavior(GazeRealizer.translateAgentTurn(0.7f, 0.7f));

   /**
    * Agent gaze directly at user.
    */
   public static final GazeBehavior USER = 
         new GazeBehavior(GazeRealizer.translateAgentTurn(0f, 0f));

   /**
    * Agent gaze toward plugin area (down and right).
    */
   public static final GazeBehavior PLUGIN = 
         new GazeBehavior(GazeRealizer.translateAgentTurn(-1f, -1f));
  
   private final Point point;

   /**
    * Given point is x-y coordinate on "retina" (camera image) of agent.
    *
    * @see GazeRealizer#translateToAgentTurnHor(Point)
    * @see GazeRealizer#translateToAgentTurnVer(Point)
    */
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
