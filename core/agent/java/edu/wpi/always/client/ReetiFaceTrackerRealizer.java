package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;
import edu.wpi.always.*;
import edu.wpi.always.client.*;

enum Directions {
   xDIRECTION, yDIRECTION, bothDIRECTIONS
}

public class ReetiFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FacePerceptor perceptor;

   private final Directions trackingDirections = Directions.bothDIRECTIONS;

   private long initialTime = 0;

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 3000;

   private static long realFaceWaitingTime = 1000;

   private static int faceAreaThreshold = 1400;

   private static int faceHorizontalDisplacementThreshold = 50;

   private static int faceVerticalDisplacementThreshold = 50;

   private boolean searchFlag = false;

   private final ReetiPIDMessages reetiPIDOutput;

   private final ReetiCommandSocketConnection client;

   private String lastMessage = "";

   public ReetiFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, CollaborationManager cm, ClientProxy proxy) {

      super(params);
      this.perceptor = perceptor;

      double InputXPID = 0;
      double InputYPID = 0;

      reetiPIDOutput = new ReetiPIDMessages();

      // get socket connection
      client = cm.getReetiSocket();
   }

   public void ReetiFaceTracking () {

      double XInputPID = 0, YInputPID = 0;

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

            XInputPID = perception.getCenter();
            YInputPID = perception.getTiltCenter();

            currentTime = System.currentTimeMillis();

            // Waiting for a second to make sure the face is still there.
            if ( (currentTime - initialTime) < realFaceWaitingTime )
               return;

            perception = perceptor.getLatest();

            // Making sure the face is not a fake one based on the awkward
            // changes in size and position.
            // if ( reetiShoreFacePerceptor.isProportionalPosition(perception,
            // prevPerception, faceHorizontalDisplacementThreshold,
            // faceVerticalDisplacementThreshold)
            // && reetiShoreFacePerceptor.isProportionalArea(perception,
            // prevPerception, faceAreaThreshold) ) {
            if ( isProportionalPosition(perception, prevPerception)
               && isProportionalArea(perception, prevPerception) ) {
               String Message = reetiPIDOutput.Track(XInputPID, YInputPID,
                     trackingDirections);

               // Making sure that the PID controller has returned different
               // control command.
               if ( !this.lastMessage.equals(Message) ) {
                  client.send(Message);
                  fireDoneMessage();
               }
               this.lastMessage = Message;
            }

            prevPerception = perception;

            this.searchFlag = true;

         } else {
            currentLosingTime = System.currentTimeMillis();

            // Waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if ( ((currentLosingTime - currentTime) > acceptableLosingTime)
               && (this.searchFlag == true) ) {
               initialTime = 0;
               String Message = reetiPIDOutput.faceSearch();
               client.send(Message);
               this.lastMessage = Message;
               this.searchFlag = false;
            }
         }
      }
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
      ReetiFaceTracking();
   }
}