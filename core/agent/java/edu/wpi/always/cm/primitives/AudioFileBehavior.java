package edu.wpi.always.cm.primitives;

import java.io.*;
import java.net.*;

import javax.sound.sampled.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.realizer.*;

public class AudioFileBehavior extends PrimitiveBehavior {

	private final URL resourceURL;

	public AudioFileBehavior(URL resourceURL) {
		this.resourceURL = resourceURL;
	}

	@Override
	public Resource getResource() {
		return Resource.Speech;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof AudioFileBehavior))
			return false;

		AudioFileBehavior theOther = (AudioFileBehavior) o;

		return this.resourceURL.getPath().equals(theOther.resourceURL.getPath());
	}

	@Override
	public int hashCode() {
		return resourceURL.getPath().hashCode();
	}

	public AudioInputStream getAudioStream() throws IOException, UnsupportedAudioFileException {
		return AudioSystem.getAudioInputStream(resourceURL);
	}

	@Override
	public String toString() {
		return "audio file (" + resourceURL.getPath() + ")";
	}

}
