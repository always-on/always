package edu.wpi.disco.rt.behavior;

import org.joda.time.DateTime;

public interface BehaviorHistory {

   boolean isDone (CompoundBehavior behavior, long since);
}
