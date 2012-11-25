package edu.wpi.always.cm.perceptors.physical.face;

import org.joda.time.DateTime;

import edu.wpi.always.cm.perceptors.EmotiveFacePerception;
import edu.wpi.always.cm.perceptors.EmotiveFacePerceptionImpl;
import edu.wpi.always.cm.perceptors.EmotiveFacePerceptor;

public class OCVEmotiveFacePerceptor implements EmotiveFacePerceptor {

	volatile EmotiveFacePerception latest;
	private final FaceDetection face;

	@Override
	public EmotiveFacePerception getLatest() {
		return latest;
	}
	
	public OCVEmotiveFacePerceptor(){
		face = new FaceDetection(0);
	}

	@Override
	public void run() {
		latest = new EmotiveFacePerceptionImpl(DateTime.now(), face.getFaceInfo(0));
	}
	
}
