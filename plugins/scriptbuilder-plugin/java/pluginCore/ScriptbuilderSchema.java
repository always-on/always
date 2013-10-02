package pluginCore;

import edu.wpi.always.cm.schemas.ActivityStateMachineSchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.*;

public class ScriptbuilderSchema extends ActivityStateMachineSchema {

   public ScriptbuilderSchema (AdjacencyPair init,
         BehaviorProposalReceiver behaviorReceiver,
			BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
			MenuPerceptor menuPerceptor) {
		 super(init, behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor);
	}
   
   @Override
   public void run () {
      // TODO: need to call cancel() when nothing left in script!
      super.run();
   }
}
