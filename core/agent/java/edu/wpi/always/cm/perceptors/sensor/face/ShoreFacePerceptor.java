package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;
import edu.wpi.disco.rt.perceptor.PerceptorBase;

public abstract class ShoreFacePerceptor extends PerceptorBase<FacePerception>
                      implements FacePerceptor {

   private long previousTime = 0;

   private final static int timeUnit = 220;

   protected FaceInfo getFaceInfo (int debug) { return null; }

   public abstract void start ();

   public abstract void stop ();

   protected FaceInfo info, prevInfo;

   private final int faceHorizontalDisplacementThreshold,
         faceVerticalDisplacementThreshold, faceAreaThreshold;

   protected ShoreFacePerceptor (int hor, int vert, int area) {
      faceHorizontalDisplacementThreshold = hor;
      faceVerticalDisplacementThreshold = vert;
      faceAreaThreshold = area;
   }

   @Override
   public void run () {
      info = getFaceInfo(0);
      if ( info != null) {
         Long currentTime = System.currentTimeMillis();
         if ( prevInfo == null || isRealFace((int) (currentTime - previousTime)) )
            latest = new FacePerception(info.intTop,
                  info.intBottom, info.intLeft, info.intRight, info.intArea,
                  info.intCenter, info.intTiltCenter);
         prevInfo = info;
         previousTime = currentTime;
      }
   }

   private boolean isRealFace (int timeDifference) {
      // avoid repeatedly creating FacePerception object when no face
      return info.intLeft != -1 && isProportionalPosition(timeDifference) && isProportionalArea(timeDifference);
   }

   private boolean isProportionalPosition (int timeDifference) {
      return ((((float) Math.abs(info.intLeft - prevInfo.intLeft) / timeDifference) <= 
             ((float) faceHorizontalDisplacementThreshold / timeUnit)) && 
             (((float) Math.abs(info.intTop - prevInfo.intTop) / timeDifference) <= 
             ((float) faceVerticalDisplacementThreshold / timeUnit)));
   }

   private boolean isProportionalArea (int timeDifference) {
      return (((float) Math.abs(info.intArea - prevInfo.intArea) / timeDifference) <= 
             ((float) faceAreaThreshold / timeUnit));
   }

   public static class Agent extends ShoreFacePerceptor {

      public Agent () {
         super(50, 50, 1700);
         start();
      }

      // accessed by both schema and realizer threads
      private volatile boolean running;

      @Override
      public synchronized void run () { // called on realizer thread
         if ( running )
            super.run();
      }

      @Override
      public synchronized void start () { // called on schema thread
         if ( !running ) {
            CPPinterface.INSTANCE.initAgentShoreEngine(0);
            running = true;
         }
      }

      @Override
      public synchronized void stop () { // called on schema thread
         if ( running ) {
            latest = null;
            running = false; // before terminate
            CPPinterface.INSTANCE.terminateAgentShoreEngine(0);
         }
      }

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         return CPPinterface.INSTANCE.getAgentFaceInfo(debug);
      }
   }

   public static class Reeti extends ShoreFacePerceptor {

      public Reeti (ReetiJsonConfiguration config) {
         super(50, 50, 1700);
         CPPinterface.INSTANCE.initReetiShoreEngine(
               new String[] { config.getIP() }, 0);
      }

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         return CPPinterface.INSTANCE.getReetiFaceInfo(debug);
      }

      @Override
      public void start () {
         throw new UnsupportedOperationException();
      }

      @Override
      public void stop () {
         CPPinterface.INSTANCE.terminateReetiShoreEngine(0);
      }
   }
}