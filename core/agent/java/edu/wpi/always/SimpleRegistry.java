package edu.wpi.always;

import org.picocontainer.MutablePicoContainer;

public interface SimpleRegistry extends Registry {

   void register (MutablePicoContainer container);
}
