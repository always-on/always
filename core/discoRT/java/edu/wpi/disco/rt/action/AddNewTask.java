package edu.wpi.disco.rt.action;

import edu.wpi.cetask.*;
import edu.wpi.disco.Disco;

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
