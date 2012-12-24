package edu.wpi.disco.rt;


/**
 * This is part of implementation to, in effect, support an extensible enumeration
 */
public class Resources implements Resource { 

   public static final Resource FOCUS = new Resource() {
      @Override
      public String toString() { return "FOCUS"; }
   };      

   public static Resource[] values = new Resource[] { FOCUS };

   public static Resource[] values () { return values; }
  
}
