package edu.wpi.disco.rt.menu;

import java.util.List;
import edu.wpi.disco.rt.schema.Schema;

public interface AdjacencyPair {

   static final String REPEAT = "What did you say?";
   
   void enter ();
   
   /**
    * Normally this should return false. When it returns true, it means that
    * nextState() is ready to return a next state now, without any text passed
    * to it (current DialogStateMachine implementation calls it with a null
    * value as text)
    */
   boolean prematureEnd ();

   /**
    * @param text can be null in case of "premature end"
    */
   AdjacencyPair nextState (String text);

   String getMessage ();

   List<String> getChoices ();

   double timeRemaining ();

   boolean isTwoColumnMenu ();
   
   Context getContext ();
   
   public static class Context {
      
      // schema for calling cancel
      private Schema schema;
      public Schema getSchema () { return schema; }
      public void setSchema (Schema schema) { this.schema = schema; }
   }
}