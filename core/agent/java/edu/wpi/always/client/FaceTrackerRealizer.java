package edu.wpi.always.client;

import edu.wpi.always.cm.perceptors.FacePerception;
import edu.wpi.always.cm.perceptors.FacePerceptor;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class FaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   public static long FACE_TRACK_TIME_DAMPENING = 1000;
   private final ClientProxy proxy;
   private final FacePerceptor perceptor;
   
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
         proxy.gaze(dir, GazeRealizer.translateToAgentTurnHor(perception.getPoint()), GazeRealizer.translateToAgentTurnVer(perception.getPoint()));
         fireDoneMessage();
      }
   }
}
