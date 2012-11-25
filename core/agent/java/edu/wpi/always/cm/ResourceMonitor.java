package edu.wpi.always.cm;

import java.util.*;

import org.joda.time.*;

import edu.wpi.always.cm.realizer.*;

public interface ResourceMonitor {

	boolean allDone(List<PrimitiveBehavior> primitives, DateTime since);
	boolean isDone(PrimitiveBehavior primitive, DateTime since);

}
