package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;

public class DummyEngagementPerceptor implements EngagementPerceptor {

   private volatile EngagementPerception latest = 
         new EngagementPerception(EngagementState.Engaged);

   @Override
   public EngagementPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {}

}
