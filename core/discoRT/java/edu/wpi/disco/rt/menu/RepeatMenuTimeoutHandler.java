package edu.wpi.disco.rt.menu;

import edu.wpi.disco.rt.util.NullArgumentException;
import java.util.List;
import java.util.regex.Pattern;

public class RepeatMenuTimeoutHandler implements MenuTimeoutHandler {

   @Override
   public AdjacencyPair handle (AdjacencyPair original) {
      if ( original == null )
         throw new NullArgumentException("original");
      if ( original instanceof RepeatAdjacencyPairWrapper )
         return original;
      return new RepeatAdjacencyPairWrapper(original);
   }

   private static class RepeatAdjacencyPairWrapper implements AdjacencyPair {

      private final AdjacencyPair inner;

      public RepeatAdjacencyPairWrapper (AdjacencyPair inner) {
         this.inner = inner;
      }

      @Override
      public void enter () {
      }

      @Override
      public boolean prematureEnd () {
         return inner.prematureEnd();
      }

      @Override
      public AdjacencyPair nextState (String text) {
         return inner.nextState(text);
      }

      private final static Pattern pattern = Pattern.compile("[a-zA-Z0-9]"); 

      @Override
      public String getMessage () {
         String original = inner.getMessage();
         if ( original == null || !pattern.matcher(original).find() )
            return original;
         return "So, " + original;
      }

      @Override
      public List<String> getChoices () {
         return inner.getChoices();
      }

      @Override
      public double timeRemaining () {
         return inner.timeRemaining();
      }

      @Override
      public boolean isTwoColumnMenu () {
         return inner.isTwoColumnMenu();
      }
   }
}
