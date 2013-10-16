package edu.wpi.always.cm.perceptors.sensor.face;

import org.joda.time.DateTime;
import java.io.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.*;

public class MirrorShoreFacePerceptor extends ShoreFacePerceptor {
   
   private final ShoreFacePerceptor agent = new ShoreFacePerceptor.Agent(),
         reeti = new ShoreFacePerceptor.Reeti();

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
    protected CPPinterface.FaceInfo getFaceInfo (int debug) {
      throw new UnsupportedOperationException();
    }
}