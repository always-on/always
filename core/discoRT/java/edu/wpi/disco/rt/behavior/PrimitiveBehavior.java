package edu.wpi.disco.rt.behavior;

import edu.wpi.disco.rt.Resource;

public abstract class PrimitiveBehavior {

   public abstract Resource getResource ();

   @Override
   public abstract boolean equals (Object o);

   @Override
   public abstract int hashCode ();

   @Override
   public String toString () {
      return getResource().toString();
   }

   private static class NullPrimitiveBehavior extends PrimitiveBehavior {

      private final Resource resource;

      public NullPrimitiveBehavior (Resource r) {
         this.resource = r;
      }

      @Override
      public Resource getResource () {
         return resource;
      }

      @Override
      public boolean equals (Object o) {
         if ( this == o )
            return true;
         if ( !(o instanceof NullPrimitiveBehavior) )
            return false;
         NullPrimitiveBehavior theOther = (NullPrimitiveBehavior) o;
         return theOther.resource.equals(this.resource);
      }

      @Override
      public int hashCode () {
         return resource.hashCode();
      }

      @Override
      public String toString () {
         return "Null<" + resource + ">";
      }
   }

   public static PrimitiveBehavior nullBehavior (Resource resource) {
      return new NullPrimitiveBehavior(resource);
   }
}
