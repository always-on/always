package edu.wpi.always.cm;

import edu.wpi.always.cm.realizer.CompoundBehavior;
import org.joda.time.DateTime;

public interface BehaviorHistory {

   boolean isDone (CompoundBehavior behavior, DateTime since);
}
