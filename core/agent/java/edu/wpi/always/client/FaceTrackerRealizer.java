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

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }

   public void AgentFaceTracking () {

      FacePerception perception, prevPerception;

      perception = perceptor.getLatest();
      prevPerception = perception;

      if ( perception != null ) {
         Point point = perception.getPoint();

         if ( point != null ) {

            // following is useful for debugging
            // java.awt.Toolkit.getDefaultToolkit().beep();

            // Happens when a face is detected for the first time.
            if ( initialTime == 0 ) {
               initialTime = System.currentTimeMillis();

               prevPerception = perception;
            }

            currentTime = System.currentTimeMillis();

            // Waiting for a second to make sure the face is still there.
            if ( (currentTime - initialTime) < realFaceWaitingTime )
               return;

            perception = perceptor.getLatest();

            // Eliminating fast moving faces above a threshold.
            if ( !isSignificantMotion(perception, prevPerception) )
               return;

            // Making sure the face is not a fake one based on the awkward
            // changes in size and position.
            if ( isProportionalPosition(perception, prevPerception)
               && isProportionalArea(perception, prevPerception) ) {
               float hor = GazeRealizer.translateToAgentTurnHor(point);
               float ver = GazeRealizer.translateToAgentTurnVer(point);
               proxy.gaze(hor, ver);
               fireDoneMessage();
            }

            prevPerception = perception;
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

   private boolean isSignificantMotion (FacePerception perception,
         FacePerception prevPerception) {
      if ( Math.abs(perception.getLeft() - prevPerception.getLeft()) > faceHorizontalMovementThreshold
         || Math.abs(perception.getTop() - prevPerception.getTop()) > faceVerticalMovementThreshold )
         return true;

      return false;
   }

   private boolean isProportionalPosition (FacePerception perception,
         FacePerception prevPerception) {
      if ( Math.abs(perception.getLeft() - prevPerception.getLeft()) <= faceHorizontalDisplacementThreshold
         && Math.abs(perception.getTop() - prevPerception.getTop()) <= faceVerticalDisplacementThreshold )
         return true;

      return false;
   }

   private boolean isProportionalArea (FacePerception perception,
         FacePerception prevPerception) {
      if ( Math.abs(perception.getArea() - perception.getArea()) <= faceAreaThreshold )
         return true;

      return false;
   }

   @Override
   public void run () {
      AgentFaceTracking();
   }
}
