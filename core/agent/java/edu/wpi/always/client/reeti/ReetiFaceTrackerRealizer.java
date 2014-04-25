package edu.wpi.always.client.reeti;

import java.awt.Point;
import edu.wpi.always.client.ClientProxy;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

enum Directions {
   xDIRECTION, yDIRECTION, bothDIRECTIONS
}

/* DESIGN NOTE: Reeti code related to face and gaze is complicated because
 * there are *three* coordinate systems in use:
 * 
 *     -image coordinates ( 0 <= x <= 160, 0 <= y <= 120 ) 
 *     -client coordinates ( -1 <= x <= +1, -1 <= y <= +1 ) 
 *     -Reeti coordinates  ( 0 <= x <= 100, 0 <= y <= 100 )
 */

public class ReetiFaceTrackerRealizer extends PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FacePerceptor perceptor;
   private final ReetiPIDMessages reetiPIDOutput;
   private final ReetiCommandSocketConnection client;
   private final Directions trackingDirections = Directions.bothDIRECTIONS;
   private final static long acceptableLosingTime = 2000;

   private long currentTime, currentLosingTime;
   private boolean searchFlag;
   private String lastMessage;

   /* DESIGN NOTE: The call to proxy.gaze() below is redundant
    * except for three situations:
    * 
    * (1) System startup (when it is useful to put neck in a known
    *     configuration, even if the neck is not exactly neutral)
    *     
    * (2) There was a gaze change hidden in an html markup (not
    *     currently used)
    * 
    * (3) If a higher-priority schema "stole" the gaze resource
    *     but didn't actually change the gaze (unlikely), in which case
    *     the gaze would re-sync to the most recent explicit gaze
    *     command.
    */
   public ReetiFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, CollaborationManager cm, 
         ReetiJsonConfiguration config, ClientProxy proxy) {
      super(params);
      this.perceptor = perceptor;
      proxy.gaze(proxy.getGazeHor(), proxy.getGazeVer());
      // see ReetiPIDControllers initialized using proxy
      reetiPIDOutput = new ReetiPIDMessages(config, proxy);
      client = cm.getReetiSocket();
   }

   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      Point point = perception == null ? null : 
         perception.isFace() ? perception.getPoint() : null;
      if ( point != null ) {

         // following is useful for debugging
         // java.awt.Toolkit.getDefaultToolkit().beep();
         currentTime = System.currentTimeMillis();
         send(reetiPIDOutput.Track(perception.getCenter(),
                                   perception.getTiltCenter(),
                                   trackingDirections));
         searchFlag = true;

      } else {

         currentLosingTime = System.currentTimeMillis();
         // waiting for the lost face for a predefined period of time looking
         // at the same direction.
         if ( ((currentLosingTime - currentTime) > acceptableLosingTime) && searchFlag ) {
            send(reetiPIDOutput.faceSearch(false));
            searchFlag = false;
         }
      }
   }
   
   private void send (String message) {
      // making sure that the PID controller has returned different
      // control command.
      if ( !message.equals(lastMessage) ) client.send(message);
      lastMessage = message;
   }
}