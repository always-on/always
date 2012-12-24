package edu.wpi.disco.rt;

public class DiscoRelatedException extends RuntimeException {

   private static final long serialVersionUID = 1628836546084217754L;

   public DiscoRelatedException () {
      super();
   }

   public DiscoRelatedException (String arg0, Throwable arg1) {
      super(arg0, arg1);
   }

   public DiscoRelatedException (String arg0) {
      super(arg0);
   }

   public DiscoRelatedException (Throwable arg0) {
      super(arg0);
   }
}
