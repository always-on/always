package edu.wpi.always.cm.perceptors;

import edu.wpi.always.cm.perceptors.physical.face.CPPinterface;
import org.joda.time.DateTime;
import java.awt.Point;

public class EmotiveFacePerceptionImpl implements EmotiveFacePerception {

   private final DateTime stamp;
   private final int happiness;
   private final int bottom;
   private final int top;
   private final int left;
   private final int right;

   public EmotiveFacePerceptionImpl (DateTime t, CPPinterface.FaceInfo faceInfo) {
      this.stamp = t;
      happiness = faceInfo.intHappiness;
      bottom = faceInfo.intBottom;
      top = faceInfo.intTop;
      left = faceInfo.intLeft;
      right = faceInfo.intRight;
   }

   @Override
   public DateTime getTimeStamp () {
      return stamp;
   }

   @Override
   public int getHappiness () {
      return happiness;
   }

   @Override
   public int getBottom () {
      return bottom;
   }

   @Override
   public int getTop () {
      return top;
   }

   @Override
   public int getLeft () {
      return left;
   }

   @Override
   public int getRight () {
      return right;
   }

   @Override
   public boolean hasFace () {
      return getHappiness() != -1;
   }

   private static final int FACE_NEAR_WIDTH_THRESHOLD = 50;// 100
   private static final int FACE_NEAR_HEIGHT_THRESHOLD = 50;// 170

   @Override
   public boolean isNear () {
      if ( !hasFace() )
         return false;
      // System.out.println("Face Width: "+(getRight()-getLeft())+" - "+"Face Height: "+(getBottom()-getTop()));
      // System.out.println(new Point((getRight()+getLeft())/2,
      // (getBottom()+getTop())/2));
      return (getRight() - getLeft()) > FACE_NEAR_WIDTH_THRESHOLD
         || (getBottom() - getTop()) > FACE_NEAR_HEIGHT_THRESHOLD;
   }

   @Override
   public Point getLocation () {
      return new Point(-(getRight() + getLeft()) / 2,
            -(getBottom() + getTop()) / 2);
   }
}
