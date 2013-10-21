package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class FaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final ClientProxy proxy;

   private final FacePerceptor perceptor;

   private long initialTime = 0;

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 2000;

   private static long realFaceWaitingTime = 1000;

   private static int faceAreaThreshold = 1700;

   private static int faceHorizontalMovementThreshold = 5;

   private static int faceVerticalMovementThreshold = 5;

   private static int faceHorizontalDisplacementThreshold = 50;

   private static int faceVerticalDisplacementThreshold = 50;

   private long[] faceProfileVector = new long[3];

   private long[] facePrevProfileVector = new long[3];

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }

   public void AgentFaceTracking () {

      FacePerception perception = perceptor.getLatest();

      if ( perception != null ) {
         Point point = perception.getPoint();

         if ( point != null ) {
      
            // following is useful for debugging
            // java.awt.Toolkit.getDefaultToolkit().beep();
            
            // Happens when a face is detected for the first time.
            if ( initialTime == 0 ) {
               initialTime = System.currentTimeMillis();

               facePrevProfileVector[0] = perception.getLeft();
               facePrevProfileVector[1] = perception.getTop();
               facePrevProfileVector[2] = perception.getArea();
            }

            currentTime = System.currentTimeMillis();

            // Waiting for a second to make sure the face is still there.
            if ( (currentTime - initialTime) < realFaceWaitingTime )
               return;

            faceProfileVector[0] = perception.getLeft();
            faceProfileVector[1] = perception.getTop();
            faceProfileVector[2] = perception.getArea();

            // Eliminating fast moving faces above a threshold.
            if ( !isSignificantMotion() )
               return;

            // Making sure the face is not a fake one based on the awkward
            // changes in size and position.
            if ( isProportionalPosition() && isProportionalArea() ) {
               float hor = GazeRealizer.translateToAgentTurnHor(point);
               float ver = GazeRealizer.translateToAgentTurnVer(point);
               proxy.gaze(hor, ver);
               fireDoneMessage();
            }

            facePrevProfileVector[0] = faceProfileVector[0];
            facePrevProfileVector[1] = faceProfileVector[1];
            facePrevProfileVector[2] = faceProfileVector[2];
         } else {
            currentLosingTime = System.currentTimeMillis();

            // Waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if ( (currentLosingTime - currentTime) > acceptableLosingTime ) {
               initialTime = 0;
               proxy.gaze(0, 0);
            }
         }
      }
   }

   private boolean isSignificantMotion () {
      if ( Math.abs(faceProfileVector[0] - facePrevProfileVector[0]) > faceHorizontalMovementThreshold
         || Math.abs(faceProfileVector[1] - facePrevProfileVector[1]) > faceVerticalMovementThreshold )
         return true;

      return false;
   }

   private boolean isProportionalPosition () {
      if ( Math.abs(faceProfileVector[0] - facePrevProfileVector[0]) <= faceHorizontalDisplacementThreshold
         && Math.abs(faceProfileVector[1] - facePrevProfileVector[1]) <= faceVerticalDisplacementThreshold )
         return true;

      return false;
   }

   private boolean isProportionalArea () {
      if ( Math.abs(faceProfileVector[2] - facePrevProfileVector[2]) <= faceAreaThreshold )
         return true;

      return false;
   }

   @Override
   public void run () {
      AgentFaceTracking();
   }
}
