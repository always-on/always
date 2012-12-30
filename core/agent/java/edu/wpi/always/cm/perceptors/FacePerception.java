package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;
import org.joda.time.DateTime;
import java.awt.Point;

public class FacePerception extends Perception {

   private final int happiness;
   private final int bottom;
   private final int top;
   private final int left;
   private final int right;

   public FacePerception (DateTime stamp, int top, int bottom, int left, int right, int happiness) {
      super(stamp);
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
      this.happiness = happiness;
   }
 
   public FacePerception (int top, int bottom, int left, int right, int happiness) {
      this(DateTime.now(), top, bottom, left, right, happiness);
   }
   
   /**
    * @return null if no face
    */
   public Point getPoint () {
      return isFace() ?  new Point(-(right + left) / 2, -(bottom + top) / 2) : null;
   }
   
   public boolean isFace () { return happiness != -1; }
   
   private static final int FACE_NEAR_WIDTH_THRESHOLD = 50;
   private static final int FACE_NEAR_HEIGHT_THRESHOLD = 50;

   public boolean isNear () {
      return isFace() && 
       ( (right - left) > FACE_NEAR_WIDTH_THRESHOLD
         || (bottom - top) > FACE_NEAR_HEIGHT_THRESHOLD );
   }
   
   public int getHappiness () { return happiness; }

   public int getBottom () { return bottom; }

   public int getTop () { return top; }

   public int getLeft () { return left; }

   public int getRight () { return right; }

   @Override
   public String toString () {
      return "FacePerception [happiness=" + happiness + ", bottom=" + bottom
         + ", top=" + top + ", left=" + left + ", right=" + right + ", stamp="
         + stamp + "]";
   }

}
