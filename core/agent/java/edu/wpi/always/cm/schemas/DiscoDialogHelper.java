package edu.wpi.always.cm.schemas;

import edu.wpi.always.cm.DiscoBootstrapper;
import edu.wpi.cetask.*;
import edu.wpi.disco.Agenda.Plugin.Item;
import edu.wpi.disco.*;
import edu.wpi.disco.rt.actions.UserGenerate;

import java.util.List;

public class DiscoDialogHelper {
   private Plan plan;

   private String lastUtterance;

   private final Disco disco;

   public DiscoDialogHelper (boolean startConsole) {
      Agent me = new Agent("agent") {
         @SuppressWarnings("unused")
         public void say (String utterance) {
            DiscoDialogHelper.this.lastUtterance = utterance;
         }
      };
      disco = new DiscoBootstrapper().bootstrap(me, startConsole);
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

   public void userItemDone (int idx, String formatted) {
      List<Item> items = generateUserTasks();
      disco.getInteraction().choose(items, idx + 1, formatted);
   }

}
