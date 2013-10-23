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

   private long currentTime = 0;

   private long currentLosingTime = 0;

   private static long acceptableLosingTime = 2000;

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

      FacePerception perception;

      perception = perceptor.getLatest();

      if ( perception != null ) {

         Point point = perception.getPoint();

         if ( point != null ) {
            // following is useful for debugging
            // java.awt.Toolkit.getDefaultToolkit().beep();

            XInputPID = perception.getCenter();
            YInputPID = perception.getTiltCenter();

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
               + XInputPID + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + YInputPID);

            currentTime = System.currentTimeMillis();

            String Message = reetiPIDOutput.Track(XInputPID, YInputPID,
                  trackingDirections);

            // Making sure that the PID controller has returned different
            // control command.
            if ( !this.lastMessage.equals(Message) ) {
               client.send(Message);
               fireDoneMessage();
            }
            this.lastMessage = Message;

            this.searchFlag = true;

         } else {
            currentLosingTime = System.currentTimeMillis();

            // Waiting for the lost face for a predefined period of time looking
            // at the same direction.
            if ( ((currentLosingTime - currentTime) > acceptableLosingTime)
               && (this.searchFlag == true) ) {
               String Message = reetiPIDOutput.faceSearch();
               client.send(Message);
               this.lastMessage = Message;
               this.searchFlag = false;
            }
         }
      }
   }

   @Override
   public void run () {
      ReetiFaceTracking();
   }
}