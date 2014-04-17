package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.PrimitiveRealizerBase;

public class FaceTrackerRealizer extends PrimitiveRealizerBase<FaceTrackBehavior> {

   private final ClientProxy proxy;

   private final FacePerceptor perceptor;

   public FaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      super(params);
      this.proxy = proxy;
      this.perceptor = perceptor;
   }

   private Point previous;
   
   private long lastSeen;

   private static long acceptableLosingTime = 4000L;
   
   private final static Point neutral = new Point(0, 0);

   @Override
   public void run () {
      FacePerception perception = perceptor.getLatest();
      Point point = perception == null ? null : perception.getPoint();
      if ( point != null ) {
         // following is useful for debugging
         // java.awt.Toolkit.getDefaultToolkit().beep();
         lastSeen = System.currentTimeMillis();
         if ( previous == null || !isClose(previous.x, point.x) || !isClose(previous.y, point. y) ) {
            proxy.gaze(GazeRealizer.translateToAgentTurnHor(point),
                       GazeRealizer.translateToAgentTurnVer(point));
            previous = point;
         } 
      } else if ( (System.currentTimeMillis() - lastSeen) > acceptableLosingTime && !neutral.equals(previous)) {
         // wait for the lost face for a predefined period of time
         // before returning to neutral
         proxy.gaze(neutral.x, neutral.y);
         previous = neutral;
      }
   }
   
   private final static int epsilon = 10;
   
   private static boolean isClose (int i, int j) { return Math.abs(i-j) <= epsilon; }
}
