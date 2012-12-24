package edu.wpi.always.cm.primitives.console;

import org.picocontainer.*;

import edu.wpi.always.PicoRegistry;
import edu.wpi.always.test.*;

public class ConsoleRealizersRegistry implements PicoRegistry {

	@Override
	public void register(MutablePicoContainer container) {
		container.addComponent(ConsoleGazeRealizer.class);
		container.addComponent(ConsoleFaceTrackerRealizer.class);
		container.addComponent(ConsoleSpeechRealizer.class);
		container.addComponent(ConsoleMenuRealizer.class);
	}

}
