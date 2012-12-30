package edu.wpi.disco.rt.action;

import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import edu.wpi.disco.rt.util.DiscoDocument;

public class LoadModelFromResource implements DiscoAction {

   private final String resourcePath;

   public LoadModelFromResource (String resourcePath) {
      this.resourcePath = resourcePath;
   }

   @Override
   public void execute (Disco disco) {
      DiscoDocument document = new DiscoDocument(disco, resourcePath);
      disco.load(null, document.getDocument(), document.getProperties(), document.getTranslate());
   }

}
