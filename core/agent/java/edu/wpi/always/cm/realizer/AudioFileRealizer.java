package edu.wpi.always.cm.realizer;

import javax.sound.sampled.*;

import edu.wpi.always.cm.primitives.*;

public class AudioFileRealizer extends SingleRunPrimitiveRealizer<AudioFileBehavior> implements LineListener {

	public AudioFileRealizer(AudioFileBehavior params) {
		super(params);
	}

	@Override
	protected void singleRun() {
		AudioInputStream inputStream;
		Clip clip = null;
		try {
			inputStream = getParams().getAudioStream();
			clip = AudioSystem.getClip();
			clip.addLineListener(this);
			clip.open(inputStream);
			clip.start();
		} catch (Exception e) {
			if(clip!=null && clip.isOpen())
				clip.close();
			e.printStackTrace();
			fireDoneMessage();
			return;
		}
	}

	@Override
	public void update(LineEvent e) {
		if(e.getType()==LineEvent.Type.STOP){
			fireDoneMessage();
			e.getLine().close();
		}
	}

}
