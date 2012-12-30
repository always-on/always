package edu.wpi.disco.rt.util;

public class NotImplementedException extends RuntimeException {

   private static final long serialVersionUID = 8937594357549352858L;

   public NotImplementedException () {
      super();
   }

   public NotImplementedException (String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public NotImplementedException (String arg0) {
      super(arg0);
   }

   public NotImplementedException (Throwable arg0) {
      super(arg0);
   }
}
