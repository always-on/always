package edu.wpi.always.client;

import edu.wpi.always.client.reeti.ReetiFaceTrackerRealizer;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.ShoreFacePerceptor;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.*;

public class MirrorFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FaceTrackerRealizer ftrAgent;

   private final ReetiFaceTrackerRealizer ftrReeti;

   public MirrorFaceTrackerRealizer (FaceTrackBehavior params,
         final ShoreFacePerceptor.Mirror perceptor, CollaborationManager cm, ClientProxy proxy) {

      super(params);
      ftrAgent = new FaceTrackerRealizer(params, perceptor, proxy);
      ftrReeti = new ReetiFaceTrackerRealizer(params, 
            new FacePerceptor () {
               @Override   
               public FacePerception getLatest () {
                  return perceptor.getReetiLatest();
               }
               @Override
               public void run() {}},  // not called by realizer
            cm, proxy);
   }

   @Override
   public void run () {

      ftrAgent.run();
      ftrReeti.run();

   }

   @Override
   public void addObserver (PrimitiveRealizerObserver observer) {
      ftrAgent.addObserver(observer);
   }

   @Override
   public void removeObserver (PrimitiveRealizerObserver observer) {
      ftrAgent.removeObserver(observer);
   }
}