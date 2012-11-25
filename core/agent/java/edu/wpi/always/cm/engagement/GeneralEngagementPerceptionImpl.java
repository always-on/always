package edu.wpi.always.cm.engagement;

import org.joda.time.*;

public class GeneralEngagementPerceptionImpl implements GeneralEngagementPerception {

	private final DateTime timeStamp;
	private final EngagementState state;

	public GeneralEngagementPerceptionImpl(EngagementState state, DateTime timeStamp) {
		this.timeStamp = timeStamp;
		this.state = state;
	}

	public GeneralEngagementPerceptionImpl(EngagementState state) {
		this(state, DateTime.now());
	}

	@Override
	public DateTime getTimeStamp() {
		return timeStamp;
	}

	@Override
	public EngagementState getState() {
		return state;
	}

	@Override
	public boolean engaged() {
		return state == EngagementState.Engaged;
	}

}
