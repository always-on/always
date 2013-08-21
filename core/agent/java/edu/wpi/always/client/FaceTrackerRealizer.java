package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class FaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   public static long FACE_TRACK_TIME_DAMPENING = 1000;
   private final ClientProxy proxy;
   private final FacePerceptor perceptor;
   
   private float lastLeft;
   private float lastArea;
   private float lastTop;
   private long lastNewNext = 0;
   private long FalseFaceTol = 0;
   private boolean facefound = false;
   private long tracktime = 0;
   private boolean FalseFace = false;
   
   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }
   
   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      if ( perception != null ) {
         Point point = perception.getPoint();
         //If Face exists in frame
         if ( point != null ) {
            float hor = GazeRealizer.translateToAgentTurnHor(point);
            float ver = GazeRealizer.translateToAgentTurnVer(point);
            float left = perception.getLeft();
            float top = perception.getTop();
            float area = perception.getArea();
            
            //If face was found for the first time
            if( facefound == false )
            {
               //Assume it's a false face the first time a face is found
               if( FalseFace == false) 
               {
                  FalseFaceTol =  System.currentTimeMillis();
                  FalseFace = true;
               }
               
               //Wait for two seconds grace time to determine whether or not it's a false face
               if( System.currentTimeMillis() - FalseFaceTol >= FACE_TRACK_TIME_DAMPENING ) 
               {
                  facefound = true;
                  lastNewNext = System.currentTimeMillis();
                  proxy.gaze(hor, ver);
                  this.lastLeft = left; this.lastTop = top; this.lastArea = area;
               }
            }
            //If face found consecutive times, apply heuristics to track it
            if ( ( facefound == true && Math.abs(left - this.lastLeft ) <= 50 ) && 

                  ( facefound == true && Math.abs(area - this.lastArea ) <= 2000 ) && 

                  (facefound == true && Math.abs(area - this.lastArea ) <= 2000 ) && 

                  ( facefound == true && Math.abs(top - this.lastTop ) <= 30 ))  {
                  // only send message if face has moved significantly
                  if ( Math.abs( left - this.lastLeft ) > 25 || Math.abs( top - this.lastTop ) > 15 ) {
                     tracktime = System.currentTimeMillis();
                     proxy.gaze(hor, ver);
                     this.lastLeft = left; this.lastTop = top; this.lastArea = area;
                  }
            }
            //Face Lost for a long time due to false faces
            else     
            {
               if ( System.currentTimeMillis() - tracktime < FACE_TRACK_TIME_DAMPENING ) {
                  facefound = false;
               }
            }
         }
         //If Face Not Found
         else if( perception.getPoint()==null )
         {
            facefound = false;
            FalseFace = false;
            if ( System.currentTimeMillis() - lastNewNext > FACE_TRACK_TIME_DAMPENING ) {
               proxy.gaze(0, 0);
            }
         }
         fireDoneMessage(); 
      }
   }
}