package edu.wpi.always.cm.perceptors.physical.face;

import org.picocontainer.MutablePicoContainer;

import edu.wpi.always.cm.perceptors.EmotiveFacePerceptor;
import edu.wpi.always.PicoRegistry;

public class OpenCVPerceptorsRegistry implements PicoRegistry {

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(EmotiveFacePerceptor.class, new OCVEmotiveFacePerceptor());
	}

}
