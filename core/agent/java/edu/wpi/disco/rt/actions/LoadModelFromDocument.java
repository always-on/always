package edu.wpi.disco.rt.actions;

import edu.wpi.cetask.TaskModel;
import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.*;
import org.w3c.dom.Document;
import java.util.Properties;

public class LoadModelFromDocument implements DiscoFunc<TaskModel> {

   private final Document doc;
   private final Properties properties;
   private final Properties translateProperties;

   public LoadModelFromDocument (Document doc, Properties properties,
         Properties translateProperties) {
      this.doc = doc;
      this.properties = properties;
      this.translateProperties = translateProperties;
   }

   public LoadModelFromDocument (DiscoDocumentSet docSet) {
      this(docSet.getMainDocument(), docSet.getProperties(), docSet
            .getTranslateProperties());
   }

   @Override
   public TaskModel execute (Disco disco) {
      return disco.load(null, doc, properties, translateProperties);
   }
}
