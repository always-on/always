package edu.wpi.always.cm.perceptors;

import edu.wpi.disco.rt.perceptor.Perception;
import java.awt.Point;

public interface EmotiveFacePerception extends Perception {

   int getHappiness ();

   int getBottom ();

   int getTop ();

   int getLeft ();

   int getRight ();

   boolean hasFace ();

   boolean isNear ();

   Point getLocation ();
}
