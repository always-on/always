package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.realizer.petri.SyncRef;

public class Constraint {

   public enum Type {
      Sync, After, Before
   }

   private final SyncRef first, second;
   private final Type type;
   /**
    * in milliseconds
    */
   private final int offset;

   public Constraint (SyncRef first, SyncRef second, Type type, int offset) {
      super();
      this.first = first;
      this.second = second;
      this.type = type;
      this.offset = offset;
   }

   public SyncRef getFirst () {
      return first;
   }

   public SyncRef getSecond () {
      return second;
   }

   public Type getType () {
      return type;
   }

   public int getOffset () {
      return offset;
   }

   @Override
   public boolean equals (Object obj) {
      if ( obj == this )
         return true;
      if ( !(obj instanceof Constraint) )
         return false;
      Constraint theOther = (Constraint) obj;
      if ( !theOther.type.equals(this.type) )
         return false;
      if ( theOther.offset != this.offset )
         return false;
      if ( !theOther.first.equals(this.first) )
         return false;
      if ( !theOther.second.equals(this.second) )
         return false;
      return true;
   }

   @Override
   public int hashCode () {
      int h = type.hashCode();
      h += 31 * h + offset;
      h += 31 * h + first.hashCode();
      h += 31 * h + second.hashCode();
      return h;
   }
}
