package edu.wpi.always.cm;

import java.util.*;

public interface ArbitrationStrategy {
	CandidateBehavior decide (List<CandidateBehavior> candidates);

	CandidateBehavior decide (List<CandidateBehavior> candidates,
			CandidateBehavior focusedOne);
}
