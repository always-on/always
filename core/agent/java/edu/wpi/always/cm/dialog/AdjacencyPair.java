package edu.wpi.always.cm.dialog;

import java.util.*;

public interface AdjacencyPair {

   public abstract void enter ();

   /**
    * Normally this should return false. When it returns true, it means that
    * nextState() is ready to return a next state now, without any text passed
    * to it (current DialogStateMachine implementation calls it with a null
    * value as text)
    */
   public abstract boolean prematureEnd ();

   /**
    * @param text can be null in case of "premature end"
    */
   public abstract AdjacencyPair nextState (String text);

   public abstract String getMessage ();

   public abstract List<String> getChoices ();

   public abstract double timeRemaining ();

   public abstract boolean isTwoColumnMenu ();
}