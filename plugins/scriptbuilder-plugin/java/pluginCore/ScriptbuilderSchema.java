package pluginCore;

import edu.wpi.always.Logger;
import edu.wpi.always.client.UIMessageDispatcher;
import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class ScriptbuilderSchema extends
		ActivityStateMachineSchema<RAGStateContext> {

	private final RAGStateContext context;

	public ScriptbuilderSchema(ScriptbuilderCoreScript init,
			BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor, UIMessageDispatcher dispatcher,
			Logger.Activity loggerName) {
		super(init, behaviorReceiver, behaviorHistory, resourceMonitor,
				menuPerceptor, loggerName);
		this.context = init.context;
	}

	@Override
	public void runActivity () {
		if (context.isDone) {
			context.resetPluginStatus();
			stop();
		} else
			super.runActivity();
	}

	@Override
	public void dispose() {
		super.dispose();
		context.resetPluginStatus();
	}

}