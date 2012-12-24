package edu.wpi.disco.rt;

public class InvalidPrimitiveBehaviorException extends RuntimeException {

   private static final long serialVersionUID = 4128796915784575391L;

   public InvalidPrimitiveBehaviorException () {
      super();
   }

   public InvalidPrimitiveBehaviorException (String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public InvalidPrimitiveBehaviorException (String arg0) {
      super(arg0);
   }

   public InvalidPrimitiveBehaviorException (Throwable arg0) {
      super(arg0);
   }
}
