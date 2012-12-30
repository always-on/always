package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.EngagementPerception.EngagementState;
import edu.wpi.disco.rt.perceptor.Perceptor;
import org.joda.time.DateTime;
import java.awt.Point;

public class DummyEngagementPerceptor implements IEngagementPerceptor {

   private volatile EngagementPerception latest;

   @Override
   public EngagementPerception getLatest () {
      return latest;
   }

   @Override
   public void run () {}

}
