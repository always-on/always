package edu.wpi.always.cm.perceptors;

import org.joda.time.*;

public class SpeechPerceptionImpl implements SpeechPerception {

	private final DateTime stamp;
	private final SpeechState speech;

	public SpeechPerceptionImpl(DateTime t, SpeechState speech) {
		this.stamp = t;
		this.speech = speech;

	}

	@Override
	public DateTime getTimeStamp() {
		return stamp;
	}

	@Override
	public SpeechState speakingState() {
		return speech;
	}

}
