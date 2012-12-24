package edu.wpi.disco.rt.actions;

import edu.wpi.cetask.*;
import edu.wpi.disco.Disco;
import edu.wpi.disco.rt.DiscoFunc;

public class AddNewTask implements DiscoFunc<Plan> {

   private final String taskId;

   public AddNewTask (String taskId) {
      this.taskId = taskId;
   }

   @Override
   public Plan execute (Disco disco) {
      Task task = disco.getTaskClass(taskId).newInstance();
      return disco.addTop(task);
   }
}
