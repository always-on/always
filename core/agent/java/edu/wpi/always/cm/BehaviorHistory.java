package edu.wpi.always.cm;

import org.joda.time.*;

import edu.wpi.always.cm.realizer.*;

public interface BehaviorHistory {
	boolean isDone(CompoundBehavior behavior, DateTime since);
}
