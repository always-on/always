package edu.wpi.always.cm.ragclient;

import org.picocontainer.*;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.cm.realizer.*;


public class RagClientRegistry implements PicoRegistry {

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(new UIMessageDispatcherImpl(new TcpConnection("localhost", 11000)));
		container.addComponent(RagGazeRealizer.class);
		container.addComponent(RagFaceExpressionRealizer.class);
		container.addComponent(RagIdleBehaviorRealizer.class);
		container.addComponent(RagFaceTrackerRealizer.class);
		container.addComponent(RagSpeechRealizer.class);
		container.addComponent(AudioFileRealizer.class);
		container.addComponent(RagMenuRealizer.class);
		container.addComponent(RagMenuPerceptor.class);
		container.as(Characteristics.CACHE).addComponent(RagKeyboard.class);
		container.as(Characteristics.CACHE).addComponent(RagCalendar.class);
		container.as(Characteristics.CACHE).addComponent(RagClientProxy.class);
	}

}
