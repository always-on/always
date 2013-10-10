package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.FaceDetection;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;
import edu.wpi.always.*;
import edu.wpi.always.client.*;

public class ReetiFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FacePerceptor perceptor;
   
   private final int trackingDirections = 2;          // 0: X Direction, 1: Y Direction, 2: Both Directions --> (Y direction is not implemented yet.)

   private long initialTime = 0;

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 2000;

   private static long realFaceWaitingTime = 1000;

   private static int faceAreaThreshold = 1700;

   private static int faceHorizontalDisplacementThreshold = 50;

   private static int faceVerticalDisplacementThreshold = 50;

   private long[] faceProfileVector = new long[3];

   private long[] facePrevProfileVector = new long[3];
   
   private boolean searchFlag = false;
   
   private final ReetiPIDMessages reetiPIDOutput;

   private final ReetiCommandSocketConnection client;

   private String lastMessage = "";

   public ReetiFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {

      super(params);
      this.perceptor = perceptor;

      double InputXPID = 0;
      double InputYPID = 0;
      
      reetiPIDOutput = new ReetiPIDMessages();

      client = new ReetiCommandSocketConnection();

   }

   public void ReetiFaceTracking () {
      double XInputPID = 0, YInputPID = 0;
      
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

            XInputPID = perception.getCenter();
            YInputPID = perception.getTiltCenter();
            
            currentTime = System.currentTimeMillis();

            // Waiting for a second to make sure the face is still there.
            if ( (currentTime - initialTime) < realFaceWaitingTime )
               return;
         
            faceProfileVector[0] = perception.getLeft();
            faceProfileVector[1] = perception.getTop();
            faceProfileVector[2] = perception.getArea();
         
            // Making sure the face is not a fake one based on the awkward
            // changes in size and position.
            if ( isProportionalPosition() && isProportionalArea() ) {
               String Message = reetiPIDOutput.Track(XInputPID, YInputPID, trackingDirections);
               
               // Making sure that the PID controller has returned different
               // control command.
               if ( !this.lastMessage.equals(Message) ) 
               {
                  client.send(Message);
                  fireDoneMessage();
               }
               this.lastMessage = Message;
            }
         
            facePrevProfileVector[0] = faceProfileVector[0];
            facePrevProfileVector[1] = faceProfileVector[1];
            facePrevProfileVector[2] = faceProfileVector[2];
            
            this.searchFlag= true; 
         
       } else {
            currentLosingTime = System.currentTimeMillis();

            // Waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if (( (currentLosingTime - currentTime) > acceptableLosingTime ) && (this.searchFlag == true) ){
               initialTime = 0;
               String Message = reetiPIDOutput.faceSearch();
               client.send(Message);
               this.lastMessage = Message;
               this.searchFlag = false;
            }
         }
     }
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
      ReetiFaceTracking();
   }
}