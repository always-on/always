package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class GazeRealizer extends SingleRunPrimitiveRealizer<GazeBehavior> {

   private final ClientProxy proxy;

   public GazeRealizer (GazeBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.gaze(translateToAgentTurnHor(getParams().getPoint()),
            translateToAgentTurnVer(getParams().getPoint()));
      fireDoneMessage();
   }

   /**
    * Inverse of {@link #translateToAgentTurnHor} and
    * {@link #translateToAgentTurnVer}.
    */
   public static Point translateAgentTurn (float hor, float ver) {
      // NB: Use jsonHpMacAddressObject.isHpMachine() to check whether the 
      // code is running on an HP machine.
      // NB: -0.0375, -0.0125, -0.075 and 0.05 are calculated due to the 
      // value of 190, 170, 220 and 120 in translateToAgentTurnHor method 
      // respectively.
      
      int offset;

      if (ClientPluginUtils.isPluginVisible())
      {
         if (hor < -0.0375)
         {
            offset = 10;
            return new Point((Math.round(160f - (160f * hor / 0.2f) + offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
         else if ((hor >= -0.0375) && (hor <= -0.0125))
         {
            offset = 60;
            return new Point((Math.round(160f - (160f * hor / 0.2f) - offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
         else
         {
            offset = 20;
            return new Point((Math.round(160f - (160f * hor / 0.2f) - offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
      }
      else
      {
         if (hor < -0.075)
         {
            offset = 20;
            return new Point((Math.round(160f - (160f * hor / 0.2f) + offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
         else if ((hor >= -0.075) && (hor <= 0.05))
         {
            offset = 70;
            return new Point((Math.round(160f - (160f * hor / 0.2f) + offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
         else
         {
            offset = 10;
            return new Point((Math.round(160f - (160f * hor / 0.2f) - offset)),
                              Math.round(120f - (120f * ver / 0.2f)));
         }
      }
   }

   /**
    * Convert camera image coordinates to horizontal component of agent
    * coordinates.
    * 
    * @return between (inclusive) -1 (agent's right) and +1 (agent's left)
    */
   public static float translateToAgentTurnHor (Point p) {
      // NB: Make sure to change translateAgentTurn if change this
      // NB: Use jsonHpMacAddressObject.isHpMachine() to check whether the 
      // code is running on an HP machine. 

      int offset;
      
      if (ClientPluginUtils.isPluginVisible())
      {
         if (p.x > 190)
         {
            offset = 10;
            return ((160f - (p.x - offset)) * 0.2f / 160f);
         }
         else if ((p.x <= 190) && (p.x >= 170))
         {
            offset = 60;
            return ((160f - (p.x + offset)) * 0.2f / 160f);
         }
         else
         {
            offset = 20;
            return ((160f - (p.x + offset)) * 0.2f / 160f);
         }
      }
      else
      {
         if(p.x > 220)
         {
            offset = 20;
            return ((160f - (p.x - offset)) * 0.2f / 160f);
         }
         else if ((p.x <= 220) && (p.x >= 120))
         {
            offset = 70;
            return ((160f - (p.x - offset)) * 0.2f / 160f);
         }
         else
         {
            offset = 10;
            return ((160f - (p.x + offset)) * 0.2f / 160f);
         }
      }
   }

   /**
    * Convert camera image coordinates to vertical component of agent
    * coordinates.
    * 
    * @return between (inclusive) -1 (down) and +1 (up)
    */
   public static float translateToAgentTurnVer (Point p) {
      // NB: Make sure to change translateAgentTurn if change this
      return (120f - p.y) * 0.2f / 120f;
   }
}