package edu.wpi.disco.rt.util;

public class NullArgumentException extends RuntimeException {

   private static final long serialVersionUID = 4462579910454182609L;
   private final String argName;

   public NullArgumentException (String argName) {
      super("Argument should not be null: " + argName);
      this.argName = argName;
   }

   public String getArgumentName () {
      return argName;
   }
}
