package edu.wpi.always.cm.realizer.petri;


public interface PlaceObserver {
	void stateChanged(Place sender, Place.State state);
}
