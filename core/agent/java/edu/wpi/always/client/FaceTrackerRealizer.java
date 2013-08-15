package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
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

   private float hor, ver;
   
   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      if ( perception != null ) {
         Point point = perception.getPoint();
         if ( point != null ) {
            float hor = GazeRealizer.translateToAgentTurnHor(point);
            float ver = GazeRealizer.translateToAgentTurnVer(point);
            // only send message if face has moved significantly
            if ( Math.abs(this.hor - hor) > 0.3f || Math.abs(this.ver - ver) > 0.2f ) {
               proxy.gaze(hor, ver);
               fireDoneMessage();
               this.hor = hor; this.ver = ver;
            }
         }
      }
   }
}
