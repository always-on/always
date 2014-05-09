package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;
import edu.wpi.disco.rt.perceptor.PerceptorBase;
import edu.wpi.disco.rt.util.Utils;

public abstract class ShoreFacePerceptor extends PerceptorBase<FacePerception>
                      implements FacePerceptor {

   private long previousTime = 0;

   private final static long timeUnit = 220;

   protected FaceInfo getFaceInfo (int debug) { return null; }

   protected FaceInfo info, prevInfo;

   private final long faceHorizontalDisplacementThreshold,
         faceVerticalDisplacementThreshold, faceAreaThreshold;

   protected ShoreFacePerceptor (int horz, int vert, int area, Object start) {
      faceHorizontalDisplacementThreshold = horz;
      faceVerticalDisplacementThreshold = vert;
      faceAreaThreshold = area;
      start(start);
   }
   
   // accessed by both schema and realizer threads
   protected volatile boolean running;

   /*
    * DESIGN NOTE: The logic below is tricky because it needs to be robust wrt
    * to both losing the face, isFace(), and also jumping to a non-real face,
    * isRealFace(). In the former case, latest should be set to null (for
    * efficiency in long-running with no face), whereas in the latter case,
    * latest should not change. However, in both cases, the value of
    * previousInfo should contain the most recently seen real face for eventual
    * proportional comparison. Note that if you don't see any real face for a long
    * time, then the proportional comparison is guaranteed to succeed because
    * the timeDifference has gotten huge.
    */

   @Override
   public void run () {
      if ( !running ) return;
      info = getFaceInfo(0);
      if ( info != null && info.isFace() ) {
         Long currentTime = System.currentTimeMillis();
         // cannot reject based on proportionality if no previous real face
         if ( prevInfo == null || isRealFace(currentTime - previousTime) ) {
            latest = new FacePerception(info.intTop,
                  info.intBottom, info.intLeft, info.intRight, info.intArea,
                  info.intCenter, info.intTiltCenter);
            prevInfo = info;
            previousTime = currentTime;
         } 
      } else latest = null;
   }

   private boolean isRealFace (long timeDifference) {
      return isProportionalPosition(timeDifference) && isProportionalArea(timeDifference);
   }

   private boolean isProportionalPosition (long timeDifference) {
      return ( Math.abs((long) (info.intLeft - prevInfo.intLeft)) / timeDifference
               <= faceHorizontalDisplacementThreshold / timeUnit ) && 
             ( Math.abs((long) (info.intTop - prevInfo.intTop)) / timeDifference
               <= faceVerticalDisplacementThreshold / timeUnit );
   }

   private boolean isProportionalArea (long timeDifference) {
      return Math.abs((long) (info.intArea - prevInfo.intArea)) / timeDifference
             <= faceAreaThreshold / timeUnit;
   }

   public synchronized void start (Object start) { // called on schema thread
      if ( !running ) {
         startEngine(start);
         running = true;
      }
   }

   abstract protected void startEngine (Object start);

   public synchronized void stop () { // called on schema thread
      if ( running ) {
         latest = null;
         running = false; // before terminate
         stopEngine();
      }
   }

   abstract protected void stopEngine (); 

   public static class Agent extends ShoreFacePerceptor {

      public Agent () { super(50, 50, 1700, null); }

      @Override
      protected void startEngine (Object start) {
         CPPinterface.INSTANCE.initAgentShoreEngine(0);
      }

      @Override
      protected void stopEngine () {
         CPPinterface.INSTANCE.terminateAgentShoreEngine(0);
      }

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         return CPPinterface.INSTANCE.getAgentFaceInfo(debug);
      }
   }

   public static class Reeti extends ShoreFacePerceptor {

      public Reeti (ReetiJsonConfiguration config) { 
         super(50, 50, 1700, config);
      }

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         return CPPinterface.INSTANCE.getReetiFaceInfo(debug);
      }

      @Override
      protected void startEngine (Object start) {
         CPPinterface.INSTANCE.initReetiShoreEngine(
               new String[] { ((ReetiJsonConfiguration) start).getIP() }, 0);
      }

      @Override
      protected void stopEngine () {
         CPPinterface.INSTANCE.terminateReetiShoreEngine(0);
      }
   }
}