package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.always.cm.realizer.SingleRunPrimitiveRealizer;
import java.awt.Point;

public class GazeRealizer extends SingleRunPrimitiveRealizer<GazeBehavior> {

   private final ClientProxy proxy;

   public GazeRealizer (GazeBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.gaze(translateToAgentTurn(getParams().getPoint()));
      fireDoneMessage();
   }

   public static AgentTurn translateToAgentTurn (Point p) {
      if ( p == null )
         return AgentTurn.Mid;
      if ( p.x > 50 )
         return AgentTurn.MidRight;
      if ( p.x < -50 )
         return AgentTurn.MidLeft;
      return AgentTurn.Mid;
   }
}
