package edu.wpi.disco.rt.menu;

import java.util.List;

public interface AdjacencyPair {

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
   
   boolean isCircular ();
}