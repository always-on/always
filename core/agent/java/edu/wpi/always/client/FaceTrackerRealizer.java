package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class FaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   public static long FACE_TRACK_TIME_DAMPENING = 1000;
   private final ClientProxy proxy;
   private final FacePerceptor perceptor;
   private AgentTurn lastDir;
   private AgentTurn nextDir;
   private long lastNewNext = 0;

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }

   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      if ( perception != null ) {
         AgentTurn dir = GazeRealizer.translateToAgentTurn(perception.getPoint());
        /* if ( dir != lastDir ) {
            if ( dir != nextDir ) {
               lastNewNext = System.currentTimeMillis();
               nextDir = dir;
            }*/
      //      if ( System.currentTimeMillis() - lastNewNext > FACE_TRACK_TIME_DAMPENING ) {
               // System.out.println(dir+" - "+perception.getLocation().getX());
               proxy.gaze(dir, GazeRealizer.translateToAgentTurnHor(perception.getPoint()), GazeRealizer.translateToAgentTurnVer(perception.getPoint()));
               lastDir = dir;
            //}
        // }
         fireDoneMessage();
      }
   }
}
