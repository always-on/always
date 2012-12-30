package edu.wpi.disco.rt.action;

//import com.sun.msv.datatype.xsd.Comparator;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;
import edu.wpi.disco.Agenda.Plugin.Item;
import java.util.*;

public class UserGenerate implements DiscoFunc<List<Item>> {

   private final Plan plan;

   public UserGenerate (Plan plan) {
      this.plan = plan;
   }

   @Override
   public List<Item> execute (Disco disco) {
      HashMap<Task, Item> items = new HashMap<Task, Item>();
      plan.decomposeAll();
      Interaction interaction = disco.getInteraction();
      Agenda agenda = interaction.getExternal().getAgenda();
      agenda.visit(plan, items, null);
      ArrayList<Item> result = new ArrayList<Item>(items.values());
      Collections.sort(result, new Comparator<Item>() {

         @Override
         public int compare (Item arg0, Item arg1) {
            return ((Integer) arg1.getPriority()).compareTo(arg0.getPriority());
         }
      });
      return result;
   }
}
