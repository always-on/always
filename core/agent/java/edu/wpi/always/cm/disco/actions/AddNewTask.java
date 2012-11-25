package edu.wpi.always.cm.disco.actions;


import edu.wpi.always.DiscoFunc;
import edu.wpi.cetask.*;
import edu.wpi.disco.*;

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
