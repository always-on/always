package edu.wpi.disco.rt.util;

import org.picocontainer.*;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin;
import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

public class Utils {

   // FIXME figure out why need to call visit directly and then
   //        remove these methods
   
   static Field getAgendaInteractionField ()
         throws NoSuchFieldException {
      Field field;
      field = Agenda.class.getDeclaredField("interaction");
      field.setAccessible(true);
      return field;
   }
   
   public static String getProperty (Plan plan, String propertyName) {
      return plan.getGoal().getType().getProperty("@" + propertyName);
   }

   public static Agenda createEmptyAgendaFor (Actor actor) {
      Agenda agenda = actor.getAgenda();
      Interaction interaction = getAgendaInteraction(agenda);
      if ( interaction == null )
         return null;
      Agenda a = new Agenda(actor);
      setAgendaInteraction(a, interaction);
      return a;
   }

   public static Interaction getAgendaInteraction (Agenda agenda) {
      try {
         Field interactionField = Utils.getAgendaInteractionField();
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

   public static void setAgendaInteraction (Agenda agenda,
         Interaction interaction) {
      Field field;
      try {
         field = Utils.getAgendaInteractionField();
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

   public static String listify (List<?> list) {
      StringBuffer buffer = new StringBuffer();
      boolean first = true;
      for (Object object : list) {
         if ( first )
            first = false;
         else
            buffer.append(", ");
         buffer.append(object);
      }
      return buffer.toString();
   }

   private Utils () {}
}
