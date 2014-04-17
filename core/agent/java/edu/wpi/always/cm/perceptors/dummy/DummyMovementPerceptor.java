package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.perceptor.PerceptorBase;

public class DummyMovementPerceptor extends PerceptorBase<MovementPerception>
             implements MovementPerceptor {

   @Override
   public void run () {
      latest = new MovementPerception(false, null);
   }

}
