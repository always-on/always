package edu.wpi.always.client;

import java.awt.Point;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.sensor.face.FaceDetection;
import edu.wpi.always.cm.primitives.FaceTrackBehavior;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.always.*;
import edu.wpi.always.client.*;

public class MirrorFaceTrackerRealizer extends
      PrimitiveRealizerBase<FaceTrackBehavior> {

   private final FaceTrackerRealizer ftrAgent;
   private final ReetiFaceTrackerRealizer ftrReeti;
   
   public MirrorFaceTrackerRealizer (FaceTrackBehavior params,
         FacePerceptor perceptor, ClientProxy proxy) {
      
      super(params);
      ftrAgent = new FaceTrackerRealizer(params, perceptor, proxy);
      ftrReeti = new ReetiFaceTrackerRealizer(params, perceptor, proxy);
      
   }

   @Override
   public void run() {
      
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