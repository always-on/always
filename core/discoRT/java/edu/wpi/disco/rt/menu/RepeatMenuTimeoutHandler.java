package edu.wpi.disco.rt.menu;

import edu.wpi.disco.rt.util.NullArgumentException;
import java.util.List;
import java.util.regex.Pattern;

public class RepeatMenuTimeoutHandler<C extends AdjacencyPair.Context> implements MenuTimeoutHandler {

   @Override
   public AdjacencyPair handle (AdjacencyPair original) {
      if ( original == null ) throw new NullArgumentException("original");
      return original instanceof RepeatMenuTimeoutAdjacencyPairWrapper ? original :
         new RepeatMenuTimeoutAdjacencyPairWrapper<C>(original);
   }
   
   private static class RepeatMenuTimeoutAdjacencyPairWrapper<C extends AdjacencyPair.Context> extends AdjacencyPairWrapper<C> {
      
      public RepeatMenuTimeoutAdjacencyPairWrapper (AdjacencyPair inner) {
         super(inner);
      }

      private final static Pattern pattern = Pattern.compile("[a-zA-Z0-9]"); 
   
      @Override
      public String getMessage () {
         String original = inner.getMessage();
         return( original == null || !pattern.matcher(original).find()
                 // TODO: provide more control over multiple wrappings
                 || inner instanceof AdjacencyPairWrapper ) ?
            original : "So, " + original;
      }
   }
}
