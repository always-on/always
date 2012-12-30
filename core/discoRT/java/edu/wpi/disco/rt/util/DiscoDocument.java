package edu.wpi.disco.rt.util;

import org.w3c.dom.Document;
import java.util.Properties;
import edu.wpi.cetask.Utils;
import edu.wpi.disco.Disco;

public class DiscoDocument {

   private final Document document;
   private final Properties properties, translate;

   public DiscoDocument (Disco disco, String source) {
      this(disco.parse(source),
            Utils.loadProperties(Utils.replaceEndsWith(source, ".xml", ".properties")),
            Utils.loadProperties(Utils.replaceEndsWith(source, ".xml", ".translate")));
   }

   public DiscoDocument (Document document, Properties properties, Properties translate) {
      this.document = document;
      this.properties = properties;
      this.translate = translate;
   }

   public Document getDocument () {
      return document;
   }

   public Properties getProperties () {
      return properties;
   }
   
   public Properties getTranslate () {
      return translate;
   }

}
