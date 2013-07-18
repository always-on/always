package edu.wpi.always.baseball;

import edu.wpi.always.Always;
import edu.wpi.always.cm.schemas.DiscoActivitySchema;
import edu.wpi.disco.rt.ResourceMonitor;
import edu.wpi.disco.rt.behavior.*;
import edu.wpi.disco.rt.menu.MenuPerceptor;

public class BaseballSchema extends DiscoActivitySchema {

   public BaseballSchema (BehaviorProposalReceiver behaviorReceiver,
         BehaviorHistory behaviorHistory, ResourceMonitor resourceMonitor,
         MenuPerceptor menuPerceptor, Always always) {
      super(behaviorReceiver, behaviorHistory, resourceMonitor, menuPerceptor, always);
      start("_WeatherStranger");
   }
}
