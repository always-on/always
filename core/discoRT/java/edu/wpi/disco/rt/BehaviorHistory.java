package edu.wpi.disco.rt;

import edu.wpi.disco.rt.realizer.CompoundBehavior;
import org.joda.time.DateTime;

public interface BehaviorHistory {

   boolean isDone (CompoundBehavior behavior, DateTime since);
}
