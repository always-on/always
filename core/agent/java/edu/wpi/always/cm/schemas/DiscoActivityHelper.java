package edu.wpi.always.cm.schemas;

import edu.wpi.cetask.*;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.*;
import edu.wpi.disco.lang.Utterance;
import edu.wpi.disco.rt.DiscoSynchronizedWrapper;
import edu.wpi.disco.rt.action.UserGenerate;
import java.util.List;

public class DiscoActivityHelper {

   private Plan plan;
   private String lastUtterance;
   private final Disco disco;

   public DiscoActivityHelper (String consoleTitle) {
      Agent me = new Agent("agent") {

         @Override
         public void say (Utterance utterance) {
            DiscoActivityHelper.this.lastUtterance = 
                  utterance.getDisco().formatUtterance(utterance);
         }
      };
      disco = new DiscoSynchronizedWrapper(me, consoleTitle).getDisco();
   }

   public Disco getDisco () {
      return disco;
   }

   public void setTaskId (String taskId) {
      TaskClass taskClass = disco.getTaskClass(taskId);
      plan = disco.addTop(taskClass.newInstance());
   }

   public Plan getPlan () {
      return plan;
   }

   public String getlastUtterance () {
      return lastUtterance;
   }

   public List<Item> generateUserTasks () {
      return new UserGenerate(plan).execute(disco);
   }

   public void userItemDone (Item item, String formatted) {
      disco.doneUtterance(item.task, formatted);
      disco.getInteraction().done(true, item.task, item.contributes);
   }
}
