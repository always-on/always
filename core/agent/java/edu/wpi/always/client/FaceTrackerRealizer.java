package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;
import edu.wpi.always.*;
import edu.wpi.always.client.*;

public class FaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final ClientProxy proxy;

   private final FacePerceptor perceptor;

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 2000;

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>FaceTrackerRealizer...");
   }

   public void AgentFaceTracking () {

      FacePerception perception;

      perception = perceptor.getLatest();

      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Perceptoin: " + perception);
      
      if ( perception != null ) {

         Point point = perception.getPoint();

         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Point: " + point);
         
         if ( point != null ) {

            // following is useful for debugging
            // java.awt.Toolkit.getDefaultToolkit().beep();

            currentTime = System.currentTimeMillis();
            float hor = GazeRealizer.translateToAgentTurnHor(point);
            float ver = GazeRealizer.translateToAgentTurnVer(point);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>hor: " + hor + " ... ver: " + ver);
            proxy.gaze(hor, ver);
            fireDoneMessage();

         } else {
            currentLosingTime = System.currentTimeMillis();

            // Waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if ( (currentLosingTime - currentTime) > acceptableLosingTime ) {
               proxy.gaze(0, 0);
            }
         }
      }
   }

   @Override
   public void run () {
      AgentFaceTracking();
   }
}
