package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.CPPinterface.FaceInfo;

public abstract class ShoreFacePerceptor extends FacePerceptorBase implements FacePerceptor {

   protected abstract FaceInfo getFaceInfo (int debug);
   
   public abstract void start ();
   public abstract void stop ();

   protected volatile FacePerception latest;

   @Override
   public FacePerception getLatest () { return latest; }
   
   @Override
   public void run () {
      FaceInfo info = getFaceInfo(0);
      latest = info == null ? null :
         new FacePerception(DateTime.now(), 
               info.intTop, info.intBottom, info.intLeft, info.intRight, info.intArea, info.intCenter, info.intTiltCenter);
   }
   
   public static class Agent extends ShoreFacePerceptor {
      
      public Agent () { start(); }

      // accessed by both schema and realizer threads
      private boolean running;

      @Override
      public synchronized void run () {
         if ( running ) super.run();
      }

      @Override
      public synchronized void start () {
         if ( ! running ) {
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
   }
   
   public static class Reeti extends ShoreFacePerceptor {
   
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
   }    
   
   public static class Mirror extends ShoreFacePerceptor {

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
      public void start () { agent.start(); }

      @Override
      public void stop () { agent.stop(); }

      @Override
      public void run () {
         agent.run();
         reeti.run();
      }

      @Override
      protected FaceInfo getFaceInfo (int debug) {
         throw new UnsupportedOperationException();
      }
   }
}