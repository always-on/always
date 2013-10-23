package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.Always;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;

public abstract class ShoreFacePerceptor implements FacePerceptor {

   protected abstract FaceInfo getFaceInfo (int debug);

   protected abstract boolean isRealFace (FaceInfo info, FaceInfo prevInfo);

   public abstract void start ();

   public abstract void stop ();

   protected static volatile FacePerception latest;

   private long initialTime = 0;

   private long currentTime = 0;

   private static long realFaceWaitingTime = 1000;

   // We only have one latest. In the mirror mode how do we know latest belongs
   // to which one, Reeti or Agent?!

   @Override
   public FacePerception getLatest () {
      return latest;
   }

   @Override
   public void run () {

      FaceInfo info = getFaceInfo(0);
      FaceInfo prevInfo = info;

      if ( info != null ) {

         if ( initialTime == 0 ) {
            initialTime = System.currentTimeMillis();

            prevInfo = info;
         }

         currentTime = System.currentTimeMillis();

         if ( (currentTime - initialTime) < realFaceWaitingTime )
            return;

         info = getFaceInfo(0);

         if ( (info != null) && (isRealFace(info, prevInfo)) ) {
            latest = new FacePerception(DateTime.now(), info.intTop,
                  info.intBottom, info.intLeft, info.intRight, info.intArea,
                  info.intCenter, info.intTiltCenter);
         }

         prevInfo = info;
      }
   }

   private static boolean isSignificantMotion (FaceInfo info,
         FaceInfo prevInfo, int faceHorizontalMovementThreshold,
         int faceVerticalMovementThreshold) {
      if ( Math.abs(info.intLeft - prevInfo.intLeft) > faceHorizontalMovementThreshold
         || Math.abs(info.intTop - prevInfo.intTop) > faceVerticalMovementThreshold )
         return true;

      return false;
   }

   private static boolean isProportionalPosition (FaceInfo info,
         FaceInfo prevInfo, int faceHorizontalDisplacementThreshold,
         int faceVerticalDisplacementThreshold) {
      if ( Math.abs(info.intLeft - prevInfo.intLeft) <= faceHorizontalDisplacementThreshold
         && Math.abs(info.intTop - prevInfo.intTop) <= faceVerticalDisplacementThreshold )
         return true;

      return false;
   }

   private static boolean isProportionalArea (FaceInfo info, FaceInfo prevInfo,
         int faceAreaThreshold) {
      if ( Math.abs(info.intArea - prevInfo.intArea) <= faceAreaThreshold )
         return true;

      return false;
   }

   public static class Agent extends ShoreFacePerceptor {

      private static int faceHorizontalMovementThreshold = 5;

      private static int faceVerticalMovementThreshold = 5;

      private static int faceHorizontalDisplacementThreshold = 50;

      private static int faceVerticalDisplacementThreshold = 50;

      private static int faceAreaThreshold = 1700;

      public Agent () {
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
      protected boolean isRealFace (FaceInfo info, FaceInfo prevInfo) {
         if ( !isSignificantMotion(info, prevInfo,
               faceHorizontalMovementThreshold, faceVerticalMovementThreshold) )
            return false;

         if ( isProportionalPosition(info, prevInfo,
               faceHorizontalDisplacementThreshold,
               faceVerticalDisplacementThreshold)
            && isProportionalArea(info, prevInfo, faceAreaThreshold) ) {
            return true;
         } else {
            return false;
         }
      }
   }

   public static class Reeti extends ShoreFacePerceptor {

      private static int faceHorizontalDisplacementThreshold = 50;

      private static int faceVerticalDisplacementThreshold = 50;

      private static int faceAreaThreshold = 1700;

      public Reeti () {
         // TODO: This should come by reading user/Reeti.json
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

      @Override
      protected boolean isRealFace (FaceInfo info, FaceInfo prevInfo) {

         if ( isProportionalPosition(info, prevInfo,
               faceHorizontalDisplacementThreshold,
               faceVerticalDisplacementThreshold)
            && isProportionalArea(info, prevInfo, faceAreaThreshold) ) {
            return true;
         } else {
            return false;
         }
      }
   }

   public static class Mirror extends ShoreFacePerceptor { // How to implement
                                                           // screening in the
                                                           // mirror?

      private final ShoreFacePerceptor agent = new Agent(),
            reeti = new Reeti();

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

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         throw new UnsupportedOperationException();
      }

      @Override
      protected boolean isRealFace (FaceInfo info, FaceInfo prevInfo) {
         // TODO Auto-generated method stub
         return false;
      }
   }
}