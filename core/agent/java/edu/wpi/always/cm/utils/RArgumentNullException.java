package edu.wpi.always.cm.utils;

public class RArgumentNullException extends RuntimeException {

   private static final long serialVersionUID = 4462579910454182609L;
   private final String argName;

   public RArgumentNullException (String argName) {
      super("Argument should not have been null: " + argName);
      this.argName = argName;
   }

   public String getArgumentName () {
      return argName;
   }
}
