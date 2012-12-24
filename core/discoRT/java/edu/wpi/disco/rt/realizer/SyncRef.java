package edu.wpi.disco.rt.realizer;

import edu.wpi.disco.rt.utils.RArgumentNullException;

public class SyncRef {

   private final SyncPoint syncPoint;
   private final PrimitiveBehavior behavior;

   public SyncRef (SyncPoint syncPoint, PrimitiveBehavior behavior) {
      if ( syncPoint == null )
         throw new RArgumentNullException("syncPoint");
      if ( behavior == null )
         throw new RArgumentNullException("behavior");
      this.syncPoint = syncPoint;
      this.behavior = behavior;
   }

   public SyncPoint getSyncPoint () {
      return syncPoint;
   }

   public PrimitiveBehavior getBehavior () {
      return behavior;
   }

   @Override
   public boolean equals (Object obj) {
      if ( obj == this )
         return true;
      if ( !(obj instanceof SyncRef) )
         return false;
      SyncRef theOther = (SyncRef) obj;
      if ( !theOther.behavior.equals(this.behavior) )
         return false;
      if ( !theOther.syncPoint.equals(this.syncPoint) )
         return false;
      return true;
   }

   @Override
   public int hashCode () {
      return 31 * behavior.hashCode() + syncPoint.hashCode();
   }
}
