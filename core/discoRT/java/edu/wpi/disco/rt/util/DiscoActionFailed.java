package edu.wpi.disco.rt.util;

public class DiscoActionFailed extends RuntimeException {

   private static final long serialVersionUID = 3926749458458381970L;

   public DiscoActionFailed () {
   }

   public DiscoActionFailed (String arg0) {
      super(arg0);
   }

   public DiscoActionFailed (Throwable arg0) {
      super(arg0);
   }

   public DiscoActionFailed (String arg0, Throwable arg1) {
      super(arg0, arg1);
   }
}
