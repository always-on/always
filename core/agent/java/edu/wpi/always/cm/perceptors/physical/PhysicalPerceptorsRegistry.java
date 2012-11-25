package edu.wpi.always.cm.perceptors.physical;

import org.picocontainer.*;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.cm.perceptors.physical.face.*;
import edu.wpi.always.cm.perceptors.physical.pir.*;
import edu.wpi.always.cm.perceptors.physical.speech.*;
import edu.wpi.always.cm.perceptors.*;


public class PhysicalPerceptorsRegistry implements PicoRegistry {

	@Override
	public void register(MutablePicoContainer container) {
		container.as(Characteristics.CACHE).addComponent(MotionPerceptor.class, PIRMotionPerceptor.class);
		container.as(Characteristics.CACHE).addComponent(SpeechPerceptor.class, LaunSpeechPerceptor.class);
		container.as(Characteristics.CACHE).addComponent(EmotiveFacePerceptor.class, OCVEmotiveFacePerceptor.class);
	}

}
