package edu.wpi.always.cm.perceptors.sensor.face;

import edu.wpi.always.cm.perceptors.*;

public abstract class FacePerceptorBase implements FacePerceptor {

   public boolean isSignificantMotion (FacePerception perception,
         FacePerception prevPerception, int faceHorizontalMovementThreshold,
         int faceVerticalMovementThreshold) {
      if ( Math.abs(perception.getLeft() - prevPerception.getLeft()) > faceHorizontalMovementThreshold
         || Math.abs(perception.getTop() - prevPerception.getTop()) > faceVerticalMovementThreshold )
         return true;

      return false;
   }

   public boolean isProportionalPosition (FacePerception perception,
         FacePerception prevPerception,
         int faceHorizontalDisplacementThreshold,
         int faceVerticalDisplacementThreshold) {
      if ( Math.abs(perception.getLeft() - prevPerception.getLeft()) <= faceHorizontalDisplacementThreshold
         && Math.abs(perception.getTop() - prevPerception.getTop()) <= faceVerticalDisplacementThreshold )
         return true;

      return false;
   }

   public boolean isProportionalArea (FacePerception perception,
         FacePerception prevPerception, int faceAreaThreshold) {
      if ( Math.abs(perception.getArea() - perception.getArea()) <= faceAreaThreshold )
         return true;

      return false;
   }
}
