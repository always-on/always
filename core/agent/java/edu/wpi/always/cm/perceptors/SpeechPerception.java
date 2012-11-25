package edu.wpi.always.cm.perceptors;

import edu.wpi.always.cm.*;

public interface SpeechPerception extends Perception {
	public enum SpeechState{
		Silent,
		Normal,
		Loud
	}
	public SpeechState speakingState();
}
