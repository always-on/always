package edu.wpi.disco.rt;

public class FuzzyException extends RuntimeException {

   private static final long serialVersionUID = -7691439671931595485L;

   public FuzzyException () {
   }

   public FuzzyException (String arg0) {
      super(arg0);
   }

   public FuzzyException (Throwable arg0) {
      super(arg0);
   }

   public FuzzyException (String arg0, Throwable arg1) {
      super(arg0, arg1);
   }
}
