package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.client.reeti.ReetiJsonConfiguration;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;

public abstract class ShoreFacePerceptor implements FacePerceptor {

   private long previousTime = 0;

   private final static int timeUnit = 220;

   protected FaceInfo getFaceInfo (int debug) { return null; }

   public abstract void start ();

   public abstract void stop ();

   protected volatile FacePerception latest;

   protected volatile FaceInfo info, prevInfo;

   private final int faceHorizontalDisplacementThreshold,
         faceVerticalDisplacementThreshold, faceAreaThreshold;

   protected ShoreFacePerceptor (int hor, int vert, int area) {
      faceHorizontalDisplacementThreshold = hor;
      faceVerticalDisplacementThreshold = vert;
      faceAreaThreshold = area;
   }

   @Override
   public FacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {
      info = getFaceInfo(0);
      Long currentTime = System.currentTimeMillis();
      if ( info != null && (prevInfo == null || isRealFace((int) (currentTime - previousTime))) )
         latest = new FacePerception(DateTime.now(), info.intTop,
               info.intBottom, info.intLeft, info.intRight, info.intArea,
               info.intCenter, info.intTiltCenter);
      else latest = null;
      prevInfo = info;
      previousTime = currentTime;
   }

   protected boolean isRealFace (int timeDifference) {
      return info.intLeft >=  0 && isProportionalPosition(timeDifference) && isProportionalArea(timeDifference);
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

      protected final int faceHorizontalMovementThreshold = 5,
            faceVerticalMovementThreshold = 5;

      public Agent () {
         super(50, 50, 1700);
         start();
      }

      // accessed by both schema and realizer threads
      private volatile boolean running;

      @Override
      public synchronized void run () {
         if ( running )
            super.run();
      }

      @Override
      public synchronized void start () {
         if ( !running ) {
            CPPinterface.INSTANCE.initAgentShoreEngine(0);
            running = true;
         }
      }

      @Override
      public synchronized void stop () {
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

      @Override
      protected boolean isRealFace (int timeDifference) {
         return super.isRealFace(timeDifference)
            && isSignificantMotion(timeDifference);
      }

      private boolean isSignificantMotion (int timeDifference) {
         return ((((float) Math.abs(info.intLeft - prevInfo.intLeft) / timeDifference) > 
                ((float) faceHorizontalMovementThreshold / timeUnit)) || 
                (((float) Math.abs(info.intTop - prevInfo.intTop) / timeDifference) > 
                ((float) faceVerticalMovementThreshold / timeUnit)));
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

   public static class Mirror extends ShoreFacePerceptor {

      private final ShoreFacePerceptor reeti, agent = new Agent();

      public Mirror (ReetiJsonConfiguration config) {
         super(0, 0, 0);
         reeti = new Reeti(config);
      }

      @Override
      public FacePerception getLatest () {
         return agent.getLatest();
      }

      public FacePerception getReetiLatest () {
         return reeti.getLatest();
      }

      @Override
      public void start () {
         agent.start();
      }

      @Override
      public void stop () {
         agent.stop();
      }

      @Override
      public void run () {
         agent.run();
         reeti.run();
      }
   }
}