package edu.wpi.disco.rt;

import edu.wpi.cetask.Plan;
import edu.wpi.disco.*;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;
import javax.xml.parsers.*;

public class DiscoUtils {

   public static void setAgendaInteraction (Agenda agenda,
         Interaction interaction) {
      Field field;
      try {
         field = getAgendaInteractionField();
         field.set(agenda, interaction);
      } catch (SecurityException e) {
         e.printStackTrace();
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   private static Field getAgendaInteractionField ()
         throws NoSuchFieldException {
      Field field;
      field = Agenda.class.getDeclaredField("interaction");
      field.setAccessible(true);
      return field;
   }

   public static Interaction getAgendaInteraction (Agenda agenda) {
      try {
         Field interactionField = getAgendaInteractionField();
         return (Interaction) interactionField.get(agenda);
      } catch (NoSuchFieldException e) {
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
      return null;
   }

   public static Agenda createEmptyAgendaFor (Actor actor) {
      Agenda agenda = actor.getAgenda();
      Interaction interaction = DiscoUtils.getAgendaInteraction(agenda);
      if ( interaction == null )
         return null;
      Agenda a = new Agenda(actor);
      DiscoUtils.setAgendaInteraction(a, interaction);
      return a;
   }

   public static DiscoDocumentSet loadDocumentFromResource (String resourcePath) {
      Document doc = null;
      Properties properties = new Properties(), translate = new Properties();
      InputStream stream = getResourceStream(resourcePath);
      if ( stream != null ) {
         try {
            doc = loadXmlDocument(stream);
         } finally {
            try {
               stream.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      if ( doc == null )
         throw new DiscoRelatedException("Cannot load model from resource: "
            + resourcePath);
      String pathWithoutExtension = FilenameUtils.removeExtension(resourcePath);
      tryLoadPropertiesFromResource(properties,
            getResourceStream(pathWithoutExtension + ".properties"));
      tryLoadPropertiesFromResource(translate,
            getResourceStream(pathWithoutExtension + ".translate.properties"));
      return new DiscoDocumentSet(doc, properties, translate);
   }

   private static InputStream getResourceStream (String path) {
      return DiscoUtils.class.getResourceAsStream(path);
   }

   public static Document loadXmlDocument (InputStream stream) {
      Document doc = null;
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory
            .newInstance();
      builderFactory.setNamespaceAware(true);
      try {
         doc = builderFactory.newDocumentBuilder().parse(stream);
      } catch (SAXException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (ParserConfigurationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return doc;
   }

   public static void tryLoadPropertiesFromResource (Properties properties,
         InputStream stream) {
      if ( stream != null ) {
         try {
            properties.load(stream);
         } catch (IOException e) {
            e.printStackTrace();
         } finally {
            try {
               stream.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public static String getProperty (Plan plan, String propertyName) {
      return plan.getGoal().getType().getProperty("@" + propertyName);
   }
}
