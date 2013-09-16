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

   public static long FACE_TRACK_TIME_DAMPENING = 1000;
   private final ClientProxy proxy;
   private final FacePerceptor perceptor;
   
   private float lastLeft;
   private float lastArea;
   private float lastTop;
   private boolean lastSearch;
   private long lastNewNext = 0;
   private long FalseFaceTolTime = 0;
   private boolean FaceFound = false;
   private boolean FalseFace = false;
   
   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
      this.lastSearch = false;   
   }
   
 //If Face Tracking for Agent or Both
   public void KarenFaceTracking()
   {
      FacePerception perception = perceptor.getLatest();
      
      if ( perception != null ) {
         Point point = perception.getPoint();
         //Face exists in frame
         if ( point != null ) {
            //Determine Facial Parameters
            float hor = GazeRealizer.translateToAgentTurnHor(point);
            float ver = GazeRealizer.translateToAgentTurnVer(point);
            float left = perception.getLeft();
            float top = perception.getTop();
            float area = perception.getArea();
            //If face was found for the first time
            if( FaceFound == false )
            {
               //Assume it's a false face 
               if( FalseFace == false) 
               {
                  FalseFaceTolTime =  System.currentTimeMillis(); //Record time at the moment the face was found
                  FalseFace = true;                               //Set False Face Flag to true
               }
               
               //Wait for FACE_TRACK_TIME_DAMPENING seconds grace time to determine whether or not it's a false face
               if( System.currentTimeMillis() - FalseFaceTolTime >= FACE_TRACK_TIME_DAMPENING ) 
               {
                  FaceFound = true;                               //Set true face found flag
                  lastNewNext = System.currentTimeMillis();       //Record time
                  proxy.gaze(hor, ver);                           //Send gaze command
                  //Update facial parameters for later comparison
                  this.lastLeft = left; this.lastTop = top; this.lastArea = area; this.lastSearch = false;  
               }
            }
            // If the face was found consecutive times and has also moved significantly 
            if ( FaceFound == true && Math.abs( left - this.lastLeft ) > 25 || Math.abs( top - this.lastTop ) > 15 ) {
               //then apply heuristics to detect a false face
               if ( Math.abs(left - this.lastLeft ) <= 75 &&    
                    Math.abs(area - this.lastArea ) <= 2000  && 
                    Math.abs(top - this.lastTop ) <= 30 )
               {
                  //send command to track it
                  proxy.gaze(hor, ver);
                  //Update last facial parameters for later comparison
                  this.lastLeft = left; this.lastTop = top; this.lastArea = area; this.lastSearch = false;
               }
            }
         }
         //Face does NOT exist in frame
         else if( point == null )
         {
            //Set command to center the agent
            int hor = 0;   
            int ver = 0;
            //Reset Flags
            FaceFound = false;
            FalseFace = false;
            //Face lost for more than FACE_TRACK_TIME_DAMPENING time
            if ( System.currentTimeMillis() - lastNewNext > FACE_TRACK_TIME_DAMPENING ) {
               //If not sent a search command already
               if(!this.lastSearch)
               {
                  proxy.gaze(hor, ver);                  //Send a search command   
                  this.lastSearch = true;                //Reset Search Flag to true
               }
            }
         }
         fireDoneMessage(); 
      }
   }
   
   @Override
   public void run () {
      KarenFaceTracking();
   }
}