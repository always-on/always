package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class FocusRequestBehavior extends PrimitiveBehavior {
	
	@Override
	public Resource getResource() {
		return Resource.Focus;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		return o.getClass().equals(FocusRequestBehavior.class);
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "Focus change request behavior";
	}

}
