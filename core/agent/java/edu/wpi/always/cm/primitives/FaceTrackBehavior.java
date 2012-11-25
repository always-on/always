package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class FaceTrackBehavior extends PrimitiveBehavior {

	@Override
	public Resource getResource() {
		return Resource.Gaze;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof FaceTrackBehavior)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "FaceTrack";
	}

}
