package edu.wpi.always.cm.realizer.petri;

import com.google.common.collect.ImmutableSet;
import java.util.*;

/**
 * @author Bahador
 */
public class Place implements Runnable {

   public enum State {
      NotActivated, Activated, ExecutionSuccessful, ExecutionFailed
   }

   private Transition input;
   private State state = State.NotActivated;
   private final Set<Transition> outputs = new HashSet<Transition>();
   private final List<PlaceObserver> observers = new ArrayList<PlaceObserver>();

   public Transition getInput () {
      return input;
   }

   public void setInput (Transition transition) {
      assert !isActivated() : "Input cannot be changed after the place is already activated.";
      Transition old = input;
      if ( old == transition )
         return;
      input = transition;
      if ( old != null )
         old.removeOutput(this);
      if ( transition != null )
         transition.addOutput(this);
   }

   public void addOutput (Transition transition) {
      outputs.add(transition);
      transition.addInput(this);
   }

   public void removeOutput (Transition transition) {
      outputs.remove(transition);
      transition.removeInput(this);
   }

   public Set<Transition> getOutputs () {
      return ImmutableSet.copyOf(outputs);
   }

   /**
    * If the Place gets activated, its execute will not be called automatically.
    * In this way, an "runner" of the PetriNet can decide, for example, to
    * execute it on another thread. Feel free to block the thread during
    * execution
    * 
    * @param transition
    */
   public void receiveToken (Transition transition) {
      if ( isActivated() )
         return;
      if ( transition.equals(input) ) {
         changeState(State.Activated);
      }
   }

   private void changeState (State s) {
      if ( state != s ) {
         state = s;
         for (PlaceObserver o : observers) {
            o.stateChanged(this, state);
         }
      }
   }

   /**
    * This is the main point of interest for subclasses of Place. This is where
    * the magic is supposed to happen! Just remember to call done() or fail()
    * when you are done.
    */
   @Override
   public void run () {
      done();
   }

   protected void done () {
      assert isActivated() : "Place.done() should be called only when the Place is activated";
      changeState(State.ExecutionSuccessful);
      for (Transition t : outputs) {
         t.receiveToken(this);
      }
   }

   protected void fail () {
      assert state != State.ExecutionSuccessful : "How did it fail after being successful?";
      changeState(State.ExecutionFailed);
   }

   public boolean isActivated () {
      return state.ordinal() >= State.Activated.ordinal();
   }

   public State getState () {
      return state;
   }

   public void addObserver (PlaceObserver o) {
      observers.add(o);
   }

   public void removeObserver (PlaceObserver o) {
      observers.remove(o);
   }

   @Override
   public String toString () {
      return "GenericPlace";
   }
}
