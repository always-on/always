package edu.wpi.disco.rt.action;

import edu.wpi.cetask.TaskModel;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.*;
import org.w3c.dom.Document;
import java.util.Properties;
import edu.wpi.disco.rt.util.DiscoDocument;

public class LoadModelFromDocument implements DiscoFunc<TaskModel> {

   private final DiscoDocument document;

   public LoadModelFromDocument (DiscoDocument model) { this.document = model; }
   
   @Override
   public TaskModel execute (Disco disco) {
      return disco.load(null, document.getDocument(), document.getProperties(), document.getTranslate());
   }
}
