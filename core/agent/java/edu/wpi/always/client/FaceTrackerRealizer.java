package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.FaceDetection;
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
   
   private static int faceAreaThreshold = 1000;
   private static int facePositionThreshold = 10;
   
   private long[] faceProfileVector = new long[4];
   private long[] facePrevProfileVector = new long[4];

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }
   
   public void AgentFaceTracking() {
      
      FacePerception perception = perceptor.getLatest();
      
      if ( perception != null ) 
      {   
         Point point = perception.getPoint();

         if ( point != null ) 
         {
       
            java.awt.Toolkit.getDefaultToolkit().beep();
  
            if (initialTime == 0)
            {  
               initialTime = System.currentTimeMillis();

               facePrevProfileVector[0] = initialTime;
               facePrevProfileVector[1] = perception.getLeft();
               facePrevProfileVector[2] = perception.getTop();
               facePrevProfileVector[3] = perception.getArea();
            }

            currentTime = System.currentTimeMillis();

            if ((currentTime - initialTime) < realFaceWaitingTime) return;

            faceProfileVector[0] = initialTime;
            faceProfileVector[1] = perception.getLeft();
            faceProfileVector[2] = perception.getTop();
            faceProfileVector[3] = perception.getArea();

            if(isProportionalPosition() && isProportionalArea())
            {
               float hor = GazeRealizer.translateToAgentTurnHor(point);
               float ver = GazeRealizer.translateToAgentTurnVer(point);
               proxy.gaze(hor, ver);
               fireDoneMessage();
            }

            facePrevProfileVector[0] = faceProfileVector[0];
            facePrevProfileVector[1] = faceProfileVector[1];
            facePrevProfileVector[2] = faceProfileVector[2];
            facePrevProfileVector[3] = faceProfileVector[3];
         }
         else
         {
            currentLosingTime = System.currentTimeMillis();
   
            if ((currentLosingTime - currentTime) > acceptableLosingTime)
            {
               initialTime = 0;
               proxy.gaze(0, 0); //Move face to default position.
            }
         }
      }
   }

   private boolean isSignificantMotion() {
      if( Math.abs( faceProfileVector[1] - facePrevProfileVector[1] ) > 10 || 
         Math.abs( faceProfileVector[2] - facePrevProfileVector[2] ) > 5 ) return true;
      
      return false;
   }
   
   private boolean isProportionalPosition(){
      if( Math.abs(faceProfileVector[1] - facePrevProfileVector[1]) <= 50 && 
         Math.abs(faceProfileVector[2] - facePrevProfileVector[2]) <= 50 ) return true;
      
      return false;
   }
   
   private boolean isProportionalArea(){
      if( Math.abs(faceProfileVector[3] - facePrevProfileVector[3]) <= 2000 ) return true;
      
      return false;
   }

   @Override
   public void run () {
      AgentFaceTracking();
   }
}
