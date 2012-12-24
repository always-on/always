package edu.wpi.always.cm.primitives;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class SpeechBehavior extends PrimitiveBehavior {

	private final String text;

	public SpeechBehavior(String text) {
		this.text = text;
	}

	@Override
	public Resource getResource() {
		return Resource.Speech;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof SpeechBehavior))
			return false;

		SpeechBehavior theOther = (SpeechBehavior) o;

		return this.getText().equals(theOther.getText());
	}

	@Override
	public int hashCode() {
		return getText().hashCode();
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Speech(\"" + text + "\")";
	}

}
