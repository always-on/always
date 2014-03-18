package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;

public class GazeRealizer extends SingleRunPrimitiveRealizer<GazeBehavior> {

   private final ClientProxy proxy;

   private static JsonHPmacAddresses jsonHpMacAddressObject = null;

   public GazeRealizer (GazeBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   public static void setjsonHpMacAddressObject(JsonHPmacAddresses jsonObj) {
      jsonHpMacAddressObject = jsonObj;
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
      // NB: 0.025 is calculated due to the value of 165 in translateToAgentTurnHor method.
      
      int offset = 10;
      
      return (jsonHpMacAddressObject.isHpMachine()) ? 
            ((ClientPluginUtils.isPluginVisible()) ?
            ((hor < -0.025) ? 
               new Point((-1) * (Math.round(160f - (160f * hor / 0.8f) - offset)),
                          Math.round(120f - (120f * ver / 0.1f)))  :
               new Point((-1) * (Math.round(160f - (160f * hor / 0.8f))),
                          Math.round(120f - (120f * ver / 0.1f)))) :
            ((hor < -0.025) ?
               new Point((-1) * (Math.round(160f - (160f * hor / 0.8f))),
                          Math.round(120f - (120f * ver / 0.1f)))  :
               new Point((-1) * (Math.round(160f - (160f * hor / 0.8f) + offset)),
                          Math.round(120f - (120f * ver / 0.1f))))) : 
            ((ClientPluginUtils.isPluginVisible()) ?
            ((hor < -0.025) ? 
               new Point((Math.round(160f - (160f * hor / 0.8f) - offset)),
                          Math.round(120f - (120f * ver / 0.1f)))  :
               new Point((Math.round(160f - (160f * hor / 0.8f))),
                          Math.round(120f - (120f * ver / 0.1f)))) :
            ((hor < -0.025) ?
               new Point((Math.round(160f - (160f * hor / 0.8f))),
                          Math.round(120f - (120f * ver / 0.1f)))  :
               new Point((Math.round(160f - (160f * hor / 0.8f) + offset)),
                          Math.round(120f - (120f * ver / 0.1f)))));
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

      int offset = 10;
      
      return (jsonHpMacAddressObject.isHpMachine()) ? 
            ((ClientPluginUtils.isPluginVisible()) ?
               ((p.x > 165) ? ((-1) * (160f - (p.x + offset)) * 0.8f / 160f) : ((-1) * (160f - p.x) * 0.8f / 160f)) :
               ((p.x > 165) ? ((-1) * (160f - p.x) * 0.8f / 160f) : ((-1) * (160f - (p.x - offset)) * 0.8f / 160f))) : 
            ((ClientPluginUtils.isPluginVisible()) ?
               ((p.x > 165) ? ((160f - (p.x + offset)) * 0.8f / 160f) : ((160f - p.x) * 0.8f / 160f)) :
               ((p.x > 165) ? ((160f - p.x) * 0.8f / 160f) : ((160f - (p.x - offset)) * 0.8f / 160f)));
   }

   /**
    * Convert camera image coordinates to vertical component of agent
    * coordinates.
    * 
    * @return between (inclusive) -1 (down) and +1 (up)
    */
   public static float translateToAgentTurnVer (Point p) {
      // NB: Make sure to change translateAgentTurn if change this
      return (120f - p.y) * 0.1f / 120f;
   }
}