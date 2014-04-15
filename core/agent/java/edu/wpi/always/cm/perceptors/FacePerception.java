package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;
import org.joda.time.DateTime;
import java.awt.Point;

public class FacePerception extends Perception {

   private final int bottom;
   private final int top;
   private final int left;
   private final int right;
   private final int center;
   private final int tiltcenter;
   private final int area;

   public FacePerception (DateTime stamp, int top, int bottom, int left, int right, int area, int center, int tiltcenter) {
      super(stamp);
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;

      this.area = area;
   	
      this.center = center;
   	
   	this.tiltcenter = tiltcenter;
   }
 
   public FacePerception (int top, int bottom, int left, int right, int area, int center, int tiltcenter) {
      this(DateTime.now(), top, bottom, left, right, area, center, tiltcenter);
   }
   
   /**
    * @return null if no face
    */
   public Point getPoint () {
      return isFace() ?  new Point(center, tiltcenter) : null;								//-(right + left) / 2, -(bottom + top) / 2) : null;
   }
   
   public boolean isFace () { return left != -1; }
   
   public static final int FACE_NEAR_AREA_THRESHOLD = 2500;

   public boolean isNear () {
      return isFace() && area > FACE_NEAR_AREA_THRESHOLD; 
   }
   
   public int getBottom () { return bottom; }

   public int getTop () { return top; }

   public int getLeft () { return left; }

   public int getRight () { return right; }
   
   public int getArea () { return area; }
   
   public int getCenter () { return center; }
   
   public int getTiltCenter () { return tiltcenter; }
   
   @Override
   public String toString () {
      return "FacePerception [bottom=" + bottom
         + ", top=" + top + ", left=" + left + ", right=" + right + ", stamp="
         + stamp + "]";
   }

}
