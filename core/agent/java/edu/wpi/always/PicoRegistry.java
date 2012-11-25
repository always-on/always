package edu.wpi.always;

import org.picocontainer.*;

public interface PicoRegistry extends Registry {
	void register (MutablePicoContainer container);
}
