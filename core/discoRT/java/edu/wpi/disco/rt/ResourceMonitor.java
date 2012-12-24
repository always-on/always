package edu.wpi.disco.rt;

import edu.wpi.disco.rt.realizer.PrimitiveBehavior;
import org.joda.time.DateTime;
import java.util.List;

public interface ResourceMonitor {

   boolean allDone (List<PrimitiveBehavior> primitives, DateTime since);

   boolean isDone (PrimitiveBehavior primitive, DateTime since);
}
