package edu.wpi.always.cm.realizer.petri;

import com.google.common.collect.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class Transition {

   private final Hashtable<Place, Boolean> inputs = new Hashtable<Place, Boolean>();
   private final Set<Place> outputs = new HashSet<Place>();
   private boolean activated;
   private ReadWriteLock lock = new ReentrantReadWriteLock();

   private boolean readyToFire () {
      lock.readLock().lock();
      try {
         boolean ready = true;
         for (Place p : inputs.keySet()) {
            if ( !inputs.get(p) ) {
               ready = false;
               break;
            }
         }
         return ready;
      } finally {
         lock.readLock().unlock();
      }
   }

   private void fire () {
      for (Place p : outputs) {
         p.receiveToken(this);
      }
   }

   public void receiveToken (Place place) {
      lock.writeLock().lock();
      try {
         inputs.put(place, true);
         if ( !activated )
            fireIfYouCan();
      } finally {
         lock.writeLock().unlock();
      }
   }

   public boolean fireIfYouCan () {
      lock.writeLock().lock();
      try {
         activated = readyToFire();
         if ( activated ) {
            fire();
            return true;
         }
         return false;
      } finally {
         lock.writeLock().unlock();
      }
   }

   public void addInput (Place place) {
      lock.writeLock().lock();
      try {
         assert !activated : "Why would anyone add more inputs to a transition after it has fired?";
         if ( !inputs.containsKey(place) ) {
            inputs.put(place, false);
            place.addOutput(this);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public void removeInput (Place place) {
      lock.writeLock().lock();
      try {
         if ( inputs.containsKey(place) ) {
            inputs.remove(place);
            place.removeOutput(this);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public Set<Place> getInputs () {
      lock.readLock().lock();
      try {
         return ImmutableSet.copyOf(inputs.keySet());
      } finally {
         lock.readLock().unlock();
      }
   }

   public void addOutput (Place place) {
      lock.writeLock().lock();
      try {
         assert !activated : "Why would anyone add more outputs to a transition after it has fired?";
         if ( !outputs.contains(place) ) {
            outputs.add(place);
            place.setInput(this);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public void removeOutput (Place place) {
      lock.writeLock().lock();
      try {
         assert !activated : "Why would anyone remove an output of a transition after it has fired?";
         outputs.remove(place);
         if ( place.getInput() == this ) {
            place.setInput(null);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public Set<Place> getOutputs () {
      lock.readLock().lock();
      try {
         return ImmutableSet.copyOf(outputs);
      } finally {
         lock.readLock().unlock();
      }
   }

   public void mergeWith (Transition theOther) {
      lock.writeLock().lock();
      try {
         List<Place> places = Lists.newArrayList(theOther.getInputs());
         for (Place p : places) {
            theOther.removeInput(p);
            this.addInput(p);
         }
         places = Lists.newArrayList(theOther.getOutputs());
         for (Place p : places) {
            theOther.removeOutput(p);
            this.addOutput(p);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   @Override
   public String toString () {
      lock.readLock().lock();
      try {
         return "tran " + asString(getInputs()) + "\n=> "
            + asString(getOutputs());
      } finally {
         lock.readLock().unlock();
      }
   }

   private String asString (Set<Place> places) {
      if ( places.isEmpty() )
         return "{}";
      StringBuffer sb = new StringBuffer();
      sb.append("{ ");
      for (Place p : places) {
         sb.append("p|").append(p).append("|, ");
      }
      return sb.delete(sb.length() - 2, sb.length()).append("}").toString();
   }
}
