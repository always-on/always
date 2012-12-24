package edu.wpi.always.test.cm;

import java.util.*;

import com.google.common.collect.*;

import edu.wpi.always.cm.*;
import edu.wpi.disco.rt.realizer.*;

public class RealizerStub implements Realizer {

	public List<CompoundBehavior> realizedBehaviors = Lists.newArrayList();

	@Override
	public void realize(CompoundBehavior behavior) {
		realizedBehaviors.add(behavior);
	}

	@Override
	public void freeUpResource(Resource r) {
	}

}
