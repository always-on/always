package edu.wpi.disco.rt;

import org.picocontainer.MutablePicoContainer;

public interface ComponentRegistry extends Registry {

   void register (MutablePicoContainer container);
}
