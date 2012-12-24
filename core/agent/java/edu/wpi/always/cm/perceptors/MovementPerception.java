package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;
import java.awt.Point;

public interface MovementPerception extends Perception {

   public Point movementLocation ();
}
