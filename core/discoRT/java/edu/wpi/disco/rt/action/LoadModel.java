package edu.wpi.disco.rt.action;

import edu.wpi.disco.Disco;

public class LoadModel implements DiscoAction {

   private final String modelPath;

   public LoadModel (String modelPath) {
      this.modelPath = modelPath;
   }

   @Override
   public void execute (Disco disco) {
      disco.load(modelPath);
   }
}
