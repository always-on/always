package pluginCore;

import com.google.gson.*;
import edu.wpi.always.client.*;
import edu.wpi.always.client.ClientPluginUtils.InstanceReuseMode;
import edu.wpi.always.user.calendar.*;
import edu.wpi.always.user.calendar.Calendar;
import edu.wpi.disco.rt.behavior.BehaviorBuilder;
import org.joda.time.*;
import org.joda.time.format.*;
import java.util.*;

public class ScriptbuilderClient implements ClientPlugin {

	// private static final String PLUGIN_NAME = "test";
	@SuppressWarnings("unused")
	private final UIMessageDispatcher dispatcher;

	public ScriptbuilderClient(UIMessageDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	// TODO the coding above should make use of ClientPlugin methods below

	@Override
	public void doAction(String actionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initInteraction() {
		// TODO Auto-generated method stub

	}

	public BehaviorBuilder updateInteraction(boolean lastProposalIsDone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void endInteraction() {
		// TODO Auto-generated method stub

	}

	@Override
	public BehaviorBuilder updateInteraction(boolean lastProposalIsDone,
			double focusMillis) {
		// TODO Auto-generated method stub
		return null;
	}
}
