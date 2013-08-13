package edu.wpi.always.cm.perceptors;

import java.awt.Point;

import org.joda.time.DateTime;

import edu.wpi.disco.rt.perceptor.Perception;

public class FacePerception extends Perception {

   private final int happiness;
   private final int bottom;
   private final int top;
   private final int left;
   private final int right;
   private final int center;
   private final int tiltcenter;

   public FacePerception (DateTime stamp, int top, int bottom, int left, int right, int happiness) {
      super(stamp);
      this.top = top;
      this.bottom = bottom;
      this.left = left;
      this.right = right;
      this.happiness = happiness;
     
   	this.center=(this.right-this.left)/2+this.left;
   	
   	this.tiltcenter=(this.bottom-this.top)/2+this.top;
   }
 
   public FacePerception (int top, int bottom, int left, int right, int happiness) {
      this(DateTime.now(), top, bottom, left, right, happiness);
   }
   
   /**
    * @return null if no face
    */
   public Point getPoint () {
      return isFace() ?  new Point(center, tiltcenter) : null;								//-(right + left) / 2, -(bottom + top) / 2) : null;
   }
   
   public boolean isFace () { return left != -1; }
   
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
