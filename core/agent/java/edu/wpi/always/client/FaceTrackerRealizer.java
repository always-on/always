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
   private final FaceDetection shore = new FaceDetection(0);
   
   //Agent
   private float lastLeft;
   private float lastArea;
   private float lastTop;
   private long lastNewNext = 0;
   private long FalseFaceTol = 0;
   private boolean facefound = false;
   private long tracktime = 0;
   private boolean FalseFace = false;
   private boolean AgentSent;
   
   //Reeti
   private ReetiPIDMessages FaceTrack;
   private JavaClient Client;
   private float lastLeftReeti;
   private float lastAreaReeti;
   private float lastTopReeti;
   private String lastMessage;
   private long lastNewNextReeti = 0;
   private long FalseFaceTolReeti = 0;
   private boolean facefoundReeti = false;
   private long tracktimeReeti = 0;
   private boolean FalseFaceReeti = false;
   private boolean ReetiSent;
   
   
   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
      
      if( Always.getAgentType() == Always.AgentType.Reeti || Always.getAgentType() == Always.AgentType.Both )
      {
         /*********PID VARIABLES***********/
         
         final int SetpointXPID = 160;
         final int SetpointYPID = 120;
   
         double InputXPID = 0;
         double InputYPID = 0;
   
         double OutputXPID = 50;  //Neutral X from JSOn File
         double OutputYPID = 55.56;  //Neutral Y from JSOn File
         
         FaceTrack = new ReetiPIDMessages( InputXPID, OutputXPID, SetpointXPID, InputYPID, OutputYPID, SetpointYPID );
         
         Client = new JavaClient();
         
         ReetiSent = false;
         
      }
      if ( Always.getAgentType() == Always.AgentType.Unity || Always.getAgentType() == Always.AgentType.Both )
         AgentSent = false;
   }
   
 //If Face Tracking for Agent or Both
   public void KarenFaceTracking()
   {
      FacePerception perception = perceptor.getLatest();
      
      if ( perception != null ) {
         Point point = perception.getPoint();
         
         //If Face exists in frame
         if ( point != null ) {
            //Determine Facial Parameters
            float hor = GazeRealizer.translateToAgentTurnHor(point);
            float ver = GazeRealizer.translateToAgentTurnVer(point);
            float left = perception.getLeft();
            float top = perception.getTop();
            float area = perception.getArea();
            
            //If face was found for the first time
            if( facefound == false )
            {
               AgentSent = false;
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
         else if( point == null )
         {
            facefound = false;
            FalseFace = false;
            if ( System.currentTimeMillis() - lastNewNext > FACE_TRACK_TIME_DAMPENING ) {
               if(AgentSent == false)
               {  proxy.gaze(0, 0);
                  AgentSent = true;
               }
            }
         }
         fireDoneMessage(); 
      }
   }

   //If Face Tracking for Reeti or Both
   public void ReetiFaceTracking()
   {
      FacePerception perception = perceptor.getReetiLatest();
      
      if ( perception != null ) {
         Point point = perception.getPoint();
         //If Face exists in frame
         if ( point != null ) {
            //Determine Facial Parameters
            double XInputPID = perception.getCenter();
            double YInputPID = perception.getTiltCenter();
            float left = perception.getLeft();
            float top = perception.getTop();
            float area = perception.getArea();
            
            //If face was found for the first time
            if( facefoundReeti == false )
            {
               ReetiSent = false;
               //Assume it's a false face the first time a face is found
               if( FalseFaceReeti == false) 
               {
                  FalseFaceTolReeti =  System.currentTimeMillis();
                  FalseFaceReeti = true;
               }
            
               //Wait for two seconds grace time to determine whether or not it's a false face
               if( System.currentTimeMillis() - FalseFaceTolReeti >= FACE_TRACK_TIME_DAMPENING ) 
               {
                  facefoundReeti = true;
                  lastNewNextReeti = System.currentTimeMillis();
                  //Track Face and Determine Command
                  String Message = FaceTrack.Track( XInputPID, YInputPID);  
                  
                  //Send Command over Socket
                  System.out.println(Message);
                  Client.send(Message);
                  this.lastLeftReeti = left; this.lastTopReeti = top; this.lastAreaReeti = area; this.lastMessage = Message;
               }
            }
            
            //If face found consecutive times, apply heuristics to track it
            if ( ( facefoundReeti == true && Math.abs(left - this.lastLeftReeti ) <= 50) && 

                  ( facefoundReeti == true && Math.abs(area - this.lastAreaReeti ) <= 1000 ) && 

                  ( facefoundReeti == true && Math.abs(top - this.lastTopReeti ) <= 50 ))  {                 
                  tracktimeReeti = System.currentTimeMillis();
                   //Track Face and Determine Command
                  String Message = FaceTrack.Track( XInputPID, YInputPID );  
                  if( !this.lastMessage.equals(Message) )
                  {
                     Client.send(Message);
                     //Send Command over Socket
                     System.out.println(Message);
                     this.lastLeftReeti = left; this.lastTopReeti = top; this.lastAreaReeti = area; this.lastMessage = Message;
                  }
              // }
            }
            
            //Face Lost for a long time due to false faces
            else     
            {
               if ( System.currentTimeMillis() - tracktimeReeti < FACE_TRACK_TIME_DAMPENING ) {
                  facefoundReeti = false;
               }
            }
         }
         
         //If Face Not Found
         else if( point == null )
         {
            facefoundReeti = false;
            FalseFaceReeti = false;
            if ( System.currentTimeMillis() - lastNewNextReeti > FACE_TRACK_TIME_DAMPENING ) {
               if(ReetiSent == false)
               {
                  String Message = FaceTrack.Search();
                  Client.send(Message);
                  System.out.println(Message);
                  ReetiSent = true;
               }
            }
         }
      }
   }
   
   @Override
   public void run () {
      
      if( Always.getAgentType() == Always.AgentType.Unity || Always.getAgentType() == Always.AgentType.Both )
      {
         KarenFaceTracking();
      }
      if( Always.getAgentType() == Always.AgentType.Reeti || Always.getAgentType() == Always.AgentType.Both )
      {
         ReetiFaceTracking();
      }
   }
   
   @Override
   public void shutdown()
   {
      shore.terminateFaceDetectionProcess(0);
   }
}