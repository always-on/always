package edu.wpi.always.client;

import edu.wpi.always.cm.primitives.GazeBehavior;
import edu.wpi.disco.rt.realizer.SingleRunPrimitiveRealizer;
import java.awt.Point;

public class GazeRealizer extends SingleRunPrimitiveRealizer<GazeBehavior> {

   private final ClientProxy proxy;

   public GazeRealizer (GazeBehavior params, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
   }

   @Override
   protected void singleRun () {
      proxy.gaze(translateToAgentTurn(getParams().getPoint()), translateToAgentTurnHor(getParams().getPoint()), translateToAgentTurnVer(getParams().getPoint()));
      fireDoneMessage();
   }

   public static AgentTurn translateToAgentTurn (Point p) {
      if ( p == null )
         return AgentTurn.Mid;
      if ( p.x > 160 )
         return AgentTurn.MidRight;
      if ( p.x < 160)
         return AgentTurn.MidLeft;
      return AgentTurn.Mid;
   }
   public static float translateToAgentTurnHor (Point p) {
      if ( p == null )
         return 0;
      
      int error = (160-p.x);
      float hor=0;
      
      if(error>0)	//turn left
      {
      	hor = (float) (error*0.30)/160;
      	return hor;
      }
      else if(error<0)	//turn right
      {
      	hor = (float) (error*0.30/160);
      	return hor;
      }
      return hor;
   }
   public static float translateToAgentTurnVer(Point p) {
   	 if ( p == null )
          return 0;
   	 
       int error = (120-p.y);
       float ver=0;
       
       if(error>0)	//turn up
       {
       	ver = (float) (error*0.30)/120;
       	return ver;
       }
       else if(error<0)	//turn down
       {
       	ver = (float) (error*0.30/120);
       	return ver;
       }
       return ver;
   }
}
