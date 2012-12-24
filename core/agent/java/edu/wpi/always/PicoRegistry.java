package edu.wpi.always;

import org.picocontainer.MutablePicoContainer;

public interface PicoRegistry extends Registry {

   void register (MutablePicoContainer container);
}
