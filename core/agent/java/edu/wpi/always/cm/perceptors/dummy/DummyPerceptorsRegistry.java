package edu.wpi.always.cm.perceptors.dummy;

import edu.wpi.always.PicoRegistry;

import org.picocontainer.*;



public class DummyPerceptorsRegistry implements PicoRegistry {

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(DummyFacePerceptor.class);
		container.addComponent(DummyMovementPerceptor.class);
	}

}
