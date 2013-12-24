package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;

public abstract class ShoreFacePerceptor implements FacePerceptor {

   protected FaceInfo getFaceInfo (int debug) {
      return null;
   }

   public abstract void start ();

   public abstract void stop ();

   protected volatile FacePerception latest;

   protected volatile FaceInfo info = null, prevInfo = null;

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

      if ( info != null ) {
         /*if (prevInfo != null)
         {*/
            if ( isRealFace() ) {
               latest = new FacePerception(DateTime.now(), info.intTop,
                     info.intBottom, info.intLeft, info.intRight, info.intArea,
                     info.intCenter, info.intTiltCenter);
            }
         //}
         prevInfo = info;
      }
   }

   protected boolean isRealFace () {
      return (isProportionalPosition() && isProportionalArea());
   }

   private boolean isProportionalPosition () {
      return (Math.abs(info.intLeft - prevInfo.intLeft) <= faceHorizontalDisplacementThreshold && Math
            .abs(info.intTop - prevInfo.intTop) <= faceVerticalDisplacementThreshold);
   }

   private boolean isProportionalArea () {
      return (Math.abs(info.intArea - prevInfo.intArea) <= faceAreaThreshold);
   }

   public static class Agent extends ShoreFacePerceptor {

      protected final int faceHorizontalMovementThreshold = 5,
            faceVerticalMovementThreshold = 5;

      public Agent () {
         super(50, 50, 1700);
         start();
      }

      // accessed by both schema and realizer threads
      private boolean running;

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
      protected boolean isRealFace () {
         return super.isRealFace() && isSignificantMotion();
      }

      private boolean isSignificantMotion () {
         return (Math.abs(info.intLeft - prevInfo.intLeft) > faceHorizontalMovementThreshold || Math
               .abs(info.intTop - prevInfo.intTop) > faceVerticalMovementThreshold);
      }
   }

   public static class Reeti extends ShoreFacePerceptor {

      public Reeti () {
         // TODO: This should come by reading user/Reeti.json
         super(50, 50, 1700);
         String[] ptr = new String[] { "130.215.28.4" };
         CPPinterface.INSTANCE.initReetiShoreEngine(ptr, 0);
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

      private final ShoreFacePerceptor agent = new Agent(),
            reeti = new Reeti();

      public Mirror () {
         super(0, 0, 0);
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