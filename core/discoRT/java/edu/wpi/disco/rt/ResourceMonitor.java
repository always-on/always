package edu.wpi.disco.rt;

import com.google.common.collect.Lists;
import edu.wpi.disco.rt.behavior.PrimitiveBehavior;
import edu.wpi.disco.rt.realizer.*;
import edu.wpi.disco.rt.util.TimeStampedValue;
import org.joda.time.DateTime;
import java.util.*;

public class ResourceMonitor implements PrimitiveBehaviorControlObserver {

   final List<TimeStampedValue<PrimitiveBehavior>> doneBehaviors
    = Collections.synchronizedList(new ArrayList<TimeStampedValue<PrimitiveBehavior>>());

   public ResourceMonitor (PrimitiveBehaviorControl realizer) {
      realizer.addObserver(this);
   }

   public boolean allDone (List<PrimitiveBehavior> primitiveBehaviors,
         DateTime since) {
      List<PrimitiveBehavior> p = new ArrayList<PrimitiveBehavior>(
            primitiveBehaviors);
      for (int i = doneBehaviors.size() - 1; i >= 0; i--) {
         TimeStampedValue<PrimitiveBehavior> cur = doneBehaviors.get(i);
         if ( cur.getTimeStamp().isBefore(since) )
            break;
         p.remove(cur.getValue());
         // If cur.getValue() was not present in p, but some other primitive
         // using the same resource was, it means that that resource has been
         // used for some other purpose in the middle execution of behaviors
         // we are looking for. ==> should return false
         // That's what we look for here
         for (PrimitiveBehavior pb : p) {
            if ( pb.getResource() == cur.getValue().getResource() )
               return false;
         }
      }
      return p.size() == 0;
   }
   
   public boolean isDone (PrimitiveBehavior primitive, DateTime since) {
      return allDone(Lists.newArrayList(primitive), since);
   }
   
   @Override
   public void primitiveDone (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
      doneBehaviors.add(new TimeStampedValue<PrimitiveBehavior>(pb));
   }

   @Override
   public void primitiveStopped (PrimitiveBehaviorControl sender,
         PrimitiveBehavior pb) {
   }

}
