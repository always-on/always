package edu.wpi.disco.rt.realizer.petri;

public interface PlaceObserver {

   void stateChanged (Place sender, Place.State state);
}
