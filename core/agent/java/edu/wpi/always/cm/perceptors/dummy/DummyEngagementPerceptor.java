package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.EngagementPerception;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.always.cm.perceptors.EngagementPerceptor;
import edu.wpi.disco.rt.perceptor.PerceptorBase;

public class DummyEngagementPerceptor extends PerceptorBase<EngagementPerception>
       implements EngagementPerceptor {

   public DummyEngagementPerceptor () {
      latest = new EngagementPerception(EngagementState.Engaged);
   }
}
