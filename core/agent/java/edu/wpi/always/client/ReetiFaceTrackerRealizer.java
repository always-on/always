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

   public static long FACE_TRACK_TIME_DAMPENING = 1000;

   private final FacePerceptor perceptor;

   private final ReetiPIDMessages faceTrack;

   private final ReetiCommandSocketConnection client;

   private float lastLeftReeti, lastAreaReeti, lastTopReeti;

   private String lastMessage;

   private long lastNewNextReeti, falseFaceTolReeti;

   private boolean facefoundReeti, falseFaceReeti;

   public ReetiFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {

      super(params);
      this.perceptor = perceptor;

      /********* PID VARIABLES ***********/

      final int SetpointXPID = 160;
      final int SetpointYPID = 120;

      double InputXPID = 0;
      double InputYPID = 0;

      double OutputXPID = 50; // TODO: This should come by reading from Reeti's
                              // json profile.
      double OutputYPID = 55.56; // TODO: This should come by reading from
                                 // Reeti's json profile.
      // initialize PID Controller
      faceTrack = new ReetiPIDMessages(InputXPID, OutputXPID, SetpointXPID,
            InputYPID, OutputYPID, SetpointYPID); 
      // initialize socket connection
      client = new ReetiCommandSocketConnection(); 

   }

   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest(); // This should be
                                                         // solved!

      if ( perception != null ) {
         Point point = perception.getPoint();
         // If Face exists in frame
         if ( point != null ) {
            // Determine Facial Parameters
            double XInputPID = perception.getCenter();
            double YInputPID = perception.getTiltCenter();
            float left = perception.getLeft();
            float top = perception.getTop();
            float area = perception.getArea();

            // If face was found for the first time
            if ( !facefoundReeti ) {
               // Assume it's a false face
               if ( !falseFaceReeti ) {
                  // Record time at the moment the face was found
                  falseFaceTolReeti = System.currentTimeMillis();
                  falseFaceReeti = true; // Set False Face Flag to true
               }

               // Wait for FACE_TRACK_TIME_DAMPENING seconds grace time to
               // determine whether or not it's a false face
               if ( System.currentTimeMillis() - falseFaceTolReeti >= FACE_TRACK_TIME_DAMPENING ) {
                  facefoundReeti = true; // Set true face found flag
                  lastNewNextReeti = System.currentTimeMillis(); // Record time
                  // Track Face using PID and Determine Command
                  String Message = faceTrack.Track(XInputPID, YInputPID);
                  // Send Command over Socket
                  client.send(Message);
                  // Update facial parameters for later comparison
                  this.lastLeftReeti = left;
                  this.lastTopReeti = top;
                  this.lastAreaReeti = area;
                  this.lastMessage = Message;
               }
            }

            // If face found consecutive times, apply heuristics to track it
            if ( facefoundReeti
               && Math.abs(left - this.lastLeftReeti) <= 50
               && Math.abs(area - this.lastAreaReeti) <= 1000
               && Math.abs(top - this.lastTopReeti) <= 50 ) {
               // Track Face using PID and Determine Command
               String Message = faceTrack.Track(XInputPID, YInputPID);
               if ( !this.lastMessage.equals(Message) ) {
                  // Send Command over Socket
                  client.send(Message);
                  // Update facial parameters for later comparison
                  this.lastLeftReeti = left;
                  this.lastTopReeti = top;
                  this.lastAreaReeti = area;
                  this.lastMessage = Message;
               }
            }
         }
         // Face does NOT exist in frame
         else if ( point == null ) {
            // Reset Flags
            facefoundReeti = false;
            falseFaceReeti = false;
            if ( System.currentTimeMillis() - lastNewNextReeti > FACE_TRACK_TIME_DAMPENING ) {
               // Track Face using PID and Determine Command
               String Message = faceTrack.Search();
               // Send Command over Socket
               client.send(Message);
            }
         }
      }
   }

}